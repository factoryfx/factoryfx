package io.github.factoryfx.docu.systemtree;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

import ch.qos.logback.classic.Level;
import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.builder.FactoryTemplateId;
import io.github.factoryfx.factory.builder.Scope;
import io.github.factoryfx.factory.log.FactoryUpdateLog;
import io.github.factoryfx.factory.merge.MergeDiffInfo;
import io.github.factoryfx.factory.storage.DataUpdate;
import io.github.factoryfx.factory.storage.StoredDataMetadata;
import io.github.factoryfx.jetty.builder.JettyFactoryTreeBuilder;
import io.github.factoryfx.jetty.builder.JettyServerRootFactory;
import io.github.factoryfx.microservice.rest.MicroserviceResourceFactory;
import io.github.factoryfx.microservice.rest.client.MicroserviceRestClient;
import io.github.factoryfx.microservice.rest.client.MicroserviceRestClientBuilder;
import io.github.factoryfx.server.Microservice;
import org.eclipse.jetty.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * two factoryfx microservices, each with its own stored configuration, coordinated by a
 * control plane that authors the system configuration in one place and deploys it over the
 * regular admin REST channel: fetch -&gt; transform -&gt; simulate -&gt; apply -&gt; compensate.
 */
public class Main {

    private static final int GREETING_PORT = 8015;
    private static final int RELAY_PORT = 8016;

    public static void main(String[] args) {
        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.ERROR);

        //two independent microservices, each with its own root, storage and admin resource
        Microservice<Server, JettyServerRootFactory> greetingService = greetingServiceBuilder().microservice().build();
        Microservice<Server, JettyServerRootFactory> relayService = relayServiceBuilder().microservice().build();
        greetingService.start();
        relayService.start();
        try {
            //the control plane administers each service over the regular admin REST channel
            MicroserviceRestClient<JettyServerRootFactory> greetingAdmin = MicroserviceRestClientBuilder.build("localhost", GREETING_PORT, JettyServerRootFactory.class);
            MicroserviceRestClient<JettyServerRootFactory> relayAdmin = MicroserviceRestClientBuilder.build("localhost", RELAY_PORT, JettyServerRootFactory.class);

            System.out.println("== coordinated deploy of the system configuration 'v1'");
            deploySystem("v1", "v1", greetingAdmin, relayAdmin, null);
            System.out.println("relay serves: " + httpGet("http://localhost:" + RELAY_PORT + "/relay"));

            System.out.println("== invalid system configuration: rejected in the simulation phase, nothing is applied anywhere");
            deploySystem("v2", "   ", greetingAdmin, relayAdmin, null);
            System.out.println("relay still serves: " + httpGet("http://localhost:" + RELAY_PORT + "/relay"));

            System.out.println("== concurrent local edit during the deploy: conflict on apply, already-updated services are reverted");
            deploySystem("v2", "v2", greetingAdmin, relayAdmin, () -> {
                //an administrator edits the greeting service directly while the system deploy is running
                DataUpdate<JettyServerRootFactory> hotfix = greetingService.prepareNewFactory();
                findChild(hotfix.root, GreetingResourceFactory.class).greeting.set("hotfix");
                greetingService.updateCurrentFactory(hotfix);
            });
            System.out.println("relay serves: " + httpGet("http://localhost:" + RELAY_PORT + "/relay"));

            System.out.println("== retry: the fresh fetch includes the hotfix, the deploy now succeeds");
            deploySystem("v2", "v2", greetingAdmin, relayAdmin, null);
            System.out.println("relay serves: " + httpGet("http://localhost:" + RELAY_PORT + "/relay"));
        } finally {
            greetingService.stop();
            relayService.stop();
        }
    }

    /**
     * the control plane's deploy pipeline. concurrentEditDuringDeploy simulates an administrator
     * editing a service between the fetch and the apply (null for the normal case).
     */
    private static void deploySystem(String greeting, String localGreeting,
                                     MicroserviceRestClient<JettyServerRootFactory> greetingAdmin,
                                     MicroserviceRestClient<JettyServerRootFactory> relayAdmin,
                                     Runnable concurrentEditDuringDeploy) {
        //contract preflight: the provider's published contract hash must match the shared api jar
        String providerContractHash = httpGet("http://localhost:" + GREETING_PORT + "/contract/" + GreetingApi.class.getName());
        if (!providerContractHash.equals(ApiContractHash.hashOf(GreetingApi.class))) {
            System.out.println("deploy aborted: GreetingApi contract mismatch");
            return;
        }

        //fetch the current configuration of every service. never rebuild it locally: fetching keeps
        //the factory ids stable, so the service-side three-way merge sees only the real delta
        DataUpdate<JettyServerRootFactory> greetingUpdate = greetingAdmin.prepareNewFactory();
        DataUpdate<JettyServerRootFactory> relayUpdate = relayAdmin.prepareNewFactory();

        //remember the current version of every service that applies before the last one:
        //those are the rollback points for compensation (here only the relay service, which applies first)
        StoredDataMetadata relayRollbackPoint = currentVersion(relayAdmin, relayUpdate.baseVersionId);

        //transform: project the system configuration into each service tree,
        //including the wiring of the typed remote reference (address + expected contract)
        findChild(greetingUpdate.root, GreetingResourceFactory.class).greeting.set(greeting);
        findChild(relayUpdate.root, RelayResourceFactory.class).localGreeting.set(localGreeting);
        GreetingApiClientFactory remoteReference = findChild(relayUpdate.root, GreetingApiClientFactory.class);
        remoteReference.host.set("localhost");
        remoteReference.port.set(GREETING_PORT);
        remoteReference.expectedContractHash.set(providerContractHash);

        //phase 1: simulate everywhere, abort before anything is applied if any service reports problems
        MergeDiffInfo<JettyServerRootFactory> greetingSimulation = greetingAdmin.simulateUpdateCurrentFactory(greetingUpdate);
        MergeDiffInfo<JettyServerRootFactory> relaySimulation = relayAdmin.simulateUpdateCurrentFactory(relayUpdate);
        if (!greetingSimulation.successfullyMerged() || greetingSimulation.hasValidationErrors()
                || !relaySimulation.successfullyMerged() || relaySimulation.hasValidationErrors()) {
            System.out.println("deploy aborted in simulation phase: " + greetingSimulation.validationErrors + relaySimulation.validationErrors);
            return;
        }

        if (concurrentEditDuringDeploy != null) {
            concurrentEditDuringDeploy.run();
        }

        //phase 2: apply on every service; if one fails, revert the already-updated ones
        FactoryUpdateLog<JettyServerRootFactory> relayLog = relayAdmin.updateCurrentFactory(relayUpdate, "system deploy");
        if (!relayLog.successfullyMerged()) {
            System.out.println("deploy failed on the relay service, nothing to compensate");
            return;
        }
        FactoryUpdateLog<JettyServerRootFactory> greetingLog = greetingAdmin.updateCurrentFactory(greetingUpdate, "system deploy");
        if (!greetingLog.successfullyMerged()) {
            String conflicts = greetingLog.mergeDiffInfo == null ? "" : " (conflicts: " + greetingLog.mergeDiffInfo.getConflictCount() + ")";
            System.out.println("deploy failed on the greeting service" + conflicts + ", reverting the relay service");
            relayAdmin.revert(relayRollbackPoint);
            return;
        }
        System.out.println("deploy successful");
    }

    private static JettyFactoryTreeBuilder greetingServiceBuilder() {
        JettyFactoryTreeBuilder builder = new JettyFactoryTreeBuilder((jetty, ctx) -> jetty
                .withHost("localhost").withPort(GREETING_PORT)
                .withResource(new FactoryTemplateId<>(MicroserviceResourceFactory.class))
                .withResource(new FactoryTemplateId<>(GreetingResourceFactory.class))
                .withResource(new FactoryTemplateId<>(ContractResourceFactory.class)));
        builder.addSingleton(GreetingResourceFactory.class, ctx -> {
            GreetingResourceFactory greetingResource = new GreetingResourceFactory();
            greetingResource.greeting.set("initial");
            return greetingResource;
        });
        builder.addSingleton(ContractResourceFactory.class, ctx -> new ContractResourceFactory());
        builder.addFactoryUnsafe(MicroserviceResourceFactory.class, Scope.SINGLETON, ctx -> new MicroserviceResourceFactory<JettyServerRootFactory>());
        return builder;
    }

    private static JettyFactoryTreeBuilder relayServiceBuilder() {
        JettyFactoryTreeBuilder builder = new JettyFactoryTreeBuilder((jetty, ctx) -> jetty
                .withHost("localhost").withPort(RELAY_PORT)
                .withResource(new FactoryTemplateId<>(MicroserviceResourceFactory.class))
                .withResource(new FactoryTemplateId<>(RelayResourceFactory.class)));
        builder.addSingleton(RelayResourceFactory.class, ctx -> {
            RelayResourceFactory relayResource = new RelayResourceFactory();
            relayResource.localGreeting.set("initial");
            relayResource.remoteGreeting.set(ctx.get(GreetingApiClientFactory.class));
            return relayResource;
        });
        builder.addSingleton(GreetingApiClientFactory.class, ctx -> {
            GreetingApiClientFactory remoteReference = new GreetingApiClientFactory();
            remoteReference.host.set("localhost");
            remoteReference.port.set(GREETING_PORT);
            return remoteReference;
        });
        builder.addFactoryUnsafe(MicroserviceResourceFactory.class, Scope.SINGLETON, ctx -> new MicroserviceResourceFactory<JettyServerRootFactory>());
        return builder;
    }

    /** a generic control plane would locate factories via FactoryTemplateId; a class filter is enough here */
    @SuppressWarnings("unchecked")
    private static <F> F findChild(FactoryBase<?, ?> root, Class<F> factoryClass) {
        return (F) root.internal().collectChildrenDeep().stream()
                .filter(factoryClass::isInstance)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("factory not found in tree: " + factoryClass.getName()));
    }

    private static StoredDataMetadata currentVersion(MicroserviceRestClient<JettyServerRootFactory> admin, String currentVersionId) {
        Optional<StoredDataMetadata> version = admin.getHistoryFactoryList(true).stream().filter(m -> m.id.equals(currentVersionId)).findFirst();
        return version.orElseThrow(() -> new IllegalStateException("current version not found in history: " + currentVersionId));
    }

    private static String httpGet(String url) {
        HttpClient httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
        try {
            return httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
