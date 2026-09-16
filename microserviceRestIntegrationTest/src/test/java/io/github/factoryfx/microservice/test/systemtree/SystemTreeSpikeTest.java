package io.github.factoryfx.microservice.test.systemtree;

import java.util.ArrayList;
import java.util.Locale;

import ch.qos.logback.classic.Level;
import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.types.EncryptedStringAttribute;
import io.github.factoryfx.factory.builder.FactoryTemplateId;
import io.github.factoryfx.factory.builder.FactoryTreeBuilder;
import io.github.factoryfx.factory.builder.Scope;
import io.github.factoryfx.factory.log.FactoryUpdateLog;
import io.github.factoryfx.factory.merge.MergeDiffInfo;
import io.github.factoryfx.factory.storage.DataUpdate;
import io.github.factoryfx.factory.storage.StoredDataMetadata;
import io.github.factoryfx.jetty.builder.SimpleJettyServerBuilder;
import io.github.factoryfx.microservice.rest.MicroserviceResourceFactory;
import io.github.factoryfx.microservice.rest.client.MicroserviceRestClient;
import io.github.factoryfx.microservice.rest.client.MicroserviceRestClientBuilder;
import io.github.factoryfx.server.Microservice;
import io.github.factoryfx.server.user.persistent.PersistentUserManagementFactory;
import io.github.factoryfx.server.user.persistent.UserFactory;
import jakarta.ws.rs.client.ClientBuilder;
import org.eclipse.jetty.server.Server;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * feasibility spike for federated configuration ("system tree") + typed cross-service references:
 * two factoryfx microservices in one JVM, a control plane (plain test code) that deploys a coordinated
 * configuration over the existing admin REST channel, a contract-hash preflight, and a compensating
 * revert after a failed deploy.
 *
 * kill criteria probed here: UUID stability of fetched-tree round-trips, revert-based rollback,
 * typed proxy injection via attribute assignment.
 */
public class SystemTreeSpikeTest {

    private static final int PORT_A = 34601;
    private static final int PORT_B = 34602;
    private static final String USER = "user123";
    private static final String PASSWORD = "pw1";

    private FactoryTreeBuilder<Server, ARoot> serviceABuilder(String passwordKey) {
        FactoryTreeBuilder<Server, ARoot> builder = new FactoryTreeBuilder<>(ARoot.class);
        builder.addBuilder(ctx -> new SimpleJettyServerBuilder<ARoot>()
                .withHost("localhost").withPort(PORT_A)
                .withResource(new FactoryTemplateId<>(MicroserviceResourceFactory.class))
                .withResource(new FactoryTemplateId<>(TimeResourceFactory.class))
                .withResource(new FactoryTemplateId<>(ContractInfoResourceFactory.class)));

        builder.addFactory(TimeResourceFactory.class, Scope.SINGLETON, ctx -> {
            TimeResourceFactory timeResource = new TimeResourceFactory();
            timeResource.greeting.set("initial");
            return timeResource;
        });
        builder.addFactory(ContractInfoResourceFactory.class, Scope.SINGLETON, ctx -> new ContractInfoResourceFactory());
        addUserManagement(builder, passwordKey);
        return builder;
    }

    private FactoryTreeBuilder<Server, BRoot> serviceBBuilder(String passwordKey) {
        FactoryTreeBuilder<Server, BRoot> builder = new FactoryTreeBuilder<>(BRoot.class);
        builder.addBuilder(ctx -> new SimpleJettyServerBuilder<BRoot>()
                .withHost("localhost").withPort(PORT_B)
                .withResource(new FactoryTemplateId<>(MicroserviceResourceFactory.class))
                .withResource(new FactoryTemplateId<>(GreetingRelayResourceFactory.class)));

        builder.addFactory(GreetingRelayResourceFactory.class, Scope.SINGLETON, ctx -> {
            GreetingRelayResourceFactory relay = new GreetingRelayResourceFactory();
            relay.localGreeting.set("initial");
            relay.remoteTime.set(ctx.getUnsafe(TimeApiClientFactory.class));
            return relay;
        });
        builder.addFactoryUnsafe(TimeApiClientFactory.class, Scope.SINGLETON, ctx -> {
            TimeApiClientFactory<BRoot> remoteReference = new TimeApiClientFactory<>();
            remoteReference.host.set("localhost");
            remoteReference.port.set(PORT_A);
            return remoteReference;
        });
        addUserManagement(builder, passwordKey);
        return builder;
    }

    private <R extends FactoryBase<?, R>> void addUserManagement(FactoryTreeBuilder<?, R> builder, String passwordKey) {
        builder.addFactoryUnsafe(MicroserviceResourceFactory.class, Scope.SINGLETON, ctx -> {
            MicroserviceResourceFactory<R> microserviceResource = new MicroserviceResourceFactory<>();
            PersistentUserManagementFactory<R> userManagement = ctx.getUnsafe(PersistentUserManagementFactory.class);
            microserviceResource.userManagement.set(userManagement);
            return microserviceResource;
        });
        builder.addFactoryUnsafe(PersistentUserManagementFactory.class, Scope.SINGLETON, ctx -> {
            PersistentUserManagementFactory<R> userManagement = new PersistentUserManagementFactory<>();
            userManagement.users.add(ctx.getUnsafe(UserFactory.class));
            return userManagement;
        });
        builder.addFactoryUnsafe(UserFactory.class, Scope.SINGLETON, ctx -> {
            UserFactory<R> user = new UserFactory<>();
            user.name.set(USER);
            user.password.setPasswordNotHashed(PASSWORD, UserFactory.passwordKey);
            user.locale.set(Locale.GERMAN);
            return user;
        });
    }

    @SuppressWarnings("unchecked")
    private <F> F findChild(FactoryBase<?, ?> root, Class<F> factoryClass) {
        return (F) root.internal().collectChildrenDeep().stream()
                .filter(factoryClass::isInstance)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("factory not found in tree: " + factoryClass.getName()));
    }

    private String httpGet(String url) {
        return ClientBuilder.newClient().target(url).request().get(String.class);
    }

    @Test
    public void system_deploy_with_contract_check_and_compensating_rollback() throws Exception {
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> e.printStackTrace());
        UserFactory.passwordKey = EncryptedStringAttribute.createKey();
        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.INFO);

        Microservice<Server, ARoot> serviceA = serviceABuilder(UserFactory.passwordKey).microservice().build();
        Microservice<Server, BRoot> serviceB = serviceBBuilder(UserFactory.passwordKey).microservice().build();
        serviceA.start();
        serviceB.start();
        try {
            Thread.sleep(2000);

            MicroserviceRestClient<ARoot> clientA = MicroserviceRestClientBuilder.build("localhost", PORT_A, USER, PASSWORD, ARoot.class);
            MicroserviceRestClient<BRoot> clientB = MicroserviceRestClientBuilder.build("localhost", PORT_B, USER, PASSWORD, BRoot.class);

            //// contract preflight: provider's published hash must match the consumer's shared api jar
            String providerContractHash = httpGet("http://localhost:" + PORT_A + "/contract/" + TimeApi.class.getName());
            Assertions.assertEquals(ApiContractHash.hashOf(TimeApi.class), providerContractHash);

            //// coordinated deploy: fetch -> transform -> simulate -> apply, on both services
            DataUpdate<ARoot> updateA = clientA.prepareNewFactory();
            findChild(updateA.root, TimeResourceFactory.class).greeting.set("v1");

            DataUpdate<BRoot> updateB = clientB.prepareNewFactory();
            GreetingRelayResourceFactory relay = findChild(updateB.root, GreetingRelayResourceFactory.class);
            relay.localGreeting.set("v1");
            TimeApiClientFactory<?> remoteReference = findChild(updateB.root, TimeApiClientFactory.class);
            remoteReference.host.set("localhost");
            remoteReference.port.set(PORT_A);
            remoteReference.expectedContractHash.set(providerContractHash);

            MergeDiffInfo<ARoot> simulateA = clientA.simulateUpdateCurrentFactory(updateA);
            Assertions.assertTrue(simulateA.successfullyMerged() && !simulateA.hasValidationErrors(), "simulate A must pass");
            MergeDiffInfo<BRoot> simulateB = clientB.simulateUpdateCurrentFactory(updateB);
            Assertions.assertTrue(simulateB.successfullyMerged() && !simulateB.hasValidationErrors(), "simulate B must pass");

            FactoryUpdateLog<ARoot> applyA = clientA.updateCurrentFactory(updateA, "system deploy v1");
            Assertions.assertTrue(applyA.successfullyMerged(), "apply A must succeed");
            FactoryUpdateLog<BRoot> applyB = clientB.updateCurrentFactory(updateB, "system deploy v1");
            Assertions.assertTrue(applyB.successfullyMerged(), "apply B must succeed");

            //// typed cross-service reference works end to end: B relays A's greeting through the proxy
            Assertions.assertEquals("v1|v1", httpGet("http://localhost:" + PORT_B + "/relay/combined"));

            //// UUID stability: a fetched tree pushed back unchanged must produce an empty, conflict-free diff
            DataUpdate<ARoot> unchanged = clientA.prepareNewFactory();
            MergeDiffInfo<ARoot> noopDiff = clientA.simulateUpdateCurrentFactory(unchanged);
            Assertions.assertTrue(noopDiff.successfullyMerged(), "no-op round-trip must not conflict");
            Assertions.assertTrue(noopDiff.mergeInfos.isEmpty(), "no-op round-trip must produce an empty diff, got: " + noopDiff.mergeInfos);

            //// failed coordinated deploy: A applies, B fails validation -> compensate by reverting A
            DataUpdate<ARoot> updateA2 = clientA.prepareNewFactory();
            StoredDataMetadata preDeployA = new ArrayList<>(clientA.getHistoryFactoryList(false)).stream()
                    .filter(m -> m.id.equals(updateA2.baseVersionId))
                    .findFirst().orElseThrow();
            findChild(updateA2.root, TimeResourceFactory.class).greeting.set("v2");
            FactoryUpdateLog<ARoot> applyA2 = clientA.updateCurrentFactory(updateA2, "system deploy v2 (will be rolled back)");
            Assertions.assertTrue(applyA2.successfullyMerged());
            Assertions.assertEquals("v1|v2", httpGet("http://localhost:" + PORT_B + "/relay/combined"));

            DataUpdate<BRoot> updateB2 = clientB.prepareNewFactory();
            GreetingRelayResourceFactory relay2 = findChild(updateB2.root, GreetingRelayResourceFactory.class);
            relay2.localGreeting.set("v2");
            relay2.poison.set("poison");
            FactoryUpdateLog<BRoot> applyB2 = clientB.updateCurrentFactory(updateB2, "system deploy v2");
            Assertions.assertTrue(applyB2.failedValidation(), "poisoned B deploy must fail server validation");

            FactoryUpdateLog<ARoot> revertLog = clientA.revert(preDeployA);
            Assertions.assertTrue(revertLog.successfullyMerged(), "compensating revert on A must succeed");

            Assertions.assertEquals("v1", findChild(clientA.prepareNewFactory().root, TimeResourceFactory.class).greeting.get(),
                    "A's config must be back at the pre-deploy state");
            Assertions.assertEquals("v1|v1", httpGet("http://localhost:" + PORT_B + "/relay/combined"),
                    "system must serve the pre-deploy state again");
        } finally {
            serviceA.stop();
            serviceB.stop();
        }
    }
}
