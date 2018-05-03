package de.factoryfx.util.rest;

import ch.qos.logback.classic.Level;
import de.factoryfx.data.attribute.types.EncryptedString;
import de.factoryfx.data.attribute.types.EncryptedStringAttribute;
import de.factoryfx.data.storage.StoredDataMetadata;
import de.factoryfx.data.storage.inmemory.InMemoryDataStorage;
import de.factoryfx.factory.FactoryManager;
import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.factory.exception.RethrowingFactoryExceptionHandler;
import de.factoryfx.jetty.HttpServerConnectorFactory;
import de.factoryfx.jetty.JettyServerFactory;
import de.factoryfx.microservice.rest.MicroserviceResource;
import de.factoryfx.microservice.rest.MicroserviceResourceFactory;
import de.factoryfx.microservice.rest.client.MicroserviceRestClient;
import de.factoryfx.microservice.rest.client.MicroserviceRestClientFactory;
import de.factoryfx.server.Microservice;

import de.factoryfx.server.user.persistent.PersistentUserManagementFactory;
import de.factoryfx.server.user.persistent.UserFactory;
import de.factoryfx.util.rest.client.RestClientFactory;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class MicroserviceRestTest {

    public static class TestJettyServer extends JettyServerFactory<Void,TestJettyServer> {
        public final FactoryReferenceAttribute<MicroserviceResource<Void, TestJettyServer,Void>, MicroserviceResourceFactory<Void, TestJettyServer,Void>> resource = new FactoryReferenceAttribute<>();
        @Override
        protected List<Object> getResourcesInstances() {
            return Arrays.asList(resource.instance());
        }
    }

    @Test
    public void integration_test() {
        String key  = new EncryptedStringAttribute().createKey();
        UserFactory.passwordKey=key;

        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.INFO);

//        ObjectMapperBuilder.build().getObjectMapper().registerSubtypes(UserManagementFactory.class);


        TestJettyServer jettyServer = new TestJettyServer();
        final HttpServerConnectorFactory<Void,TestJettyServer> httpServerConnectorFactory = new HttpServerConnectorFactory<>();
        httpServerConnectorFactory.port.set(34579);
        httpServerConnectorFactory.host.set("localhost");
        jettyServer.connectors.add(httpServerConnectorFactory);
        final MicroserviceResourceFactory<Void, TestJettyServer, Void> microserviceResource = new MicroserviceResourceFactory<>();
        jettyServer.resource.set(microserviceResource);
        final PersistentUserManagementFactory<Void,TestJettyServer> userManagement = new PersistentUserManagementFactory<>();
        final UserFactory<Void,TestJettyServer> user = new UserFactory<>();
        user.name.set("user123");
        user.password.set(new EncryptedString("hash123", key));
        user.locale.set(Locale.GERMAN);
        userManagement.users.add(user);
        microserviceResource.userManagement.set(userManagement);

        jettyServer = jettyServer.utility().prepareUsableCopy();
        Microservice<Void, TestJettyServer, Void> microservice = new Microservice<>(new FactoryManager<>(new RethrowingFactoryExceptionHandler()), new InMemoryDataStorage<>(jettyServer));
        Thread serverThread = new Thread(() -> {
            microservice.start();
        });
        serverThread.start();

        try {

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }


            MicroserviceRestClientFactory<Void, RestClientRoot, Void, TestJettyServer> microserviceRestClientFactory = new MicroserviceRestClientFactory<>();
            final RestClientFactory<Void,TestJettyServer> restClient = new RestClientFactory<>();
            restClient.port.set(34579);
            restClient.host.set("localhost");
            restClient.path.set("adminui");
            microserviceRestClientFactory.restClient.set(restClient);
            microserviceRestClientFactory.user.set("user123");
            microserviceRestClientFactory.passwordHash.set("hash123");
            microserviceRestClientFactory.factoryRootClass.set(TestJettyServer.class);

//            microserviceRestClientFactory=microserviceRestClientFactory.utility().prepareUsableCopy();

            MicroserviceRestClient<Void, TestJettyServer> microserviceRestClient = microserviceRestClientFactory.internalFactory().instance();
            microserviceRestClient.prepareNewFactory();


            final ArrayList<StoredDataMetadata> historyFactoryList = new ArrayList<>(microserviceRestClient.getHistoryFactoryList());
            microserviceRestClient.getHistoryFactory(historyFactoryList.get(0).id);

            Assert.assertEquals(Locale.GERMAN, microserviceRestClient.getLocale());
        } finally {
            microservice.stop();
        }
    }

    public static class RestClientRoot extends SimpleFactoryBase<Void,Void,RestClientRoot> {
        @Override
        public Void createImpl() {
            return null;
        }
    }


}
