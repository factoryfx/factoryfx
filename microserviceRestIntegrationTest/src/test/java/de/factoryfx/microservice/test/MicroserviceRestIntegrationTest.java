package de.factoryfx.microservice.test;

import ch.qos.logback.classic.Level;
import de.factoryfx.data.attribute.types.EncryptedString;
import de.factoryfx.data.attribute.types.EncryptedStringAttribute;
import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.data.storage.StoredDataMetadata;
import de.factoryfx.data.storage.inmemory.InMemoryDataStorage;
import de.factoryfx.factory.FactoryManager;
import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.factory.exception.RethrowingFactoryExceptionHandler;
import de.factoryfx.jetty.HttpServerConnectorFactory;
import de.factoryfx.jetty.JettyServer;
import de.factoryfx.jetty.JettyServerFactory;
import de.factoryfx.microservice.common.ResponseWorkaround;
import de.factoryfx.microservice.rest.MicroserviceResource;
import de.factoryfx.microservice.rest.MicroserviceResourceFactory;
import de.factoryfx.microservice.rest.client.MicroserviceRestClient;
import de.factoryfx.microservice.rest.client.MicroserviceRestClientFactory;
import de.factoryfx.server.Microservice;

import de.factoryfx.server.user.persistent.PersistentUserManagementFactory;
import de.factoryfx.server.user.persistent.UserFactory;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.BiConsumer;

public class MicroserviceRestIntegrationTest {

    public static class TestJettyServer extends JettyServerFactory<TestVisitor,TestJettyServer> {
        public final FactoryReferenceAttribute<MicroserviceResource<TestVisitor, TestJettyServer,Void>, MicroserviceResourceFactory<TestVisitor, TestJettyServer,Void>> resource = new FactoryReferenceAttribute<>();

        @Override
        protected List<Object> getResourcesInstances() {
            return Arrays.asList(resource.instance());
        }

        public TestJettyServer(){
            configLiveCycle().setRuntimeQueryExecutor((testVisitor, jettyServer) -> testVisitor.test="123");
        }
    }

    public static class TestVisitor  {
        public String test;
    }

    @Test
    public void integration_test() {
        String key  = new EncryptedStringAttribute().createKey();
        UserFactory.passwordKey=key;

        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.INFO);


        TestJettyServer jettyServer = new TestJettyServer();
        final HttpServerConnectorFactory<TestVisitor,TestJettyServer> httpServerConnectorFactory = new HttpServerConnectorFactory<>();
        httpServerConnectorFactory.port.set(34579);
        httpServerConnectorFactory.host.set("localhost");
        jettyServer.connectors.add(httpServerConnectorFactory);
        final MicroserviceResourceFactory<TestVisitor, TestJettyServer, Void> microserviceResource = new MicroserviceResourceFactory<>();
        jettyServer.resource.set(microserviceResource);
        final PersistentUserManagementFactory<TestVisitor,TestJettyServer> userManagement = new PersistentUserManagementFactory<>();
        final UserFactory<TestVisitor,TestJettyServer> user = new UserFactory<>();
        user.name.set("user123");
        user.password.set(new EncryptedString("hash123", key));
        user.locale.set(Locale.GERMAN);
        userManagement.users.add(user);
        microserviceResource.userManagement.set(userManagement);

        jettyServer = jettyServer.utility().prepareUsableCopy();
        Microservice<TestVisitor, TestJettyServer, Void> microservice = new Microservice<>(new FactoryManager<>(new RethrowingFactoryExceptionHandler()), new InMemoryDataStorage<>(jettyServer));
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


            MicroserviceRestClientFactory<Void, RestClientRoot, TestVisitor, TestJettyServer,Void> microserviceRestClientFactory = new MicroserviceRestClientFactory<>();

            microserviceRestClientFactory.port.set(34579);
            microserviceRestClientFactory.host.set("localhost");
//            microserviceRestClientFactory.path.set("microservice");
            microserviceRestClientFactory.user.set("user123");
            microserviceRestClientFactory.passwordHash.set("hash123");
            microserviceRestClientFactory.factoryRootClass.set(TestJettyServer.class);

//            microserviceRestClientFactory=microserviceRestClientFactory.utility().prepareUsableCopy();

            MicroserviceRestClient<TestVisitor, TestJettyServer,Void> microserviceRestClient = microserviceRestClientFactory.internalFactory().instance();
            microserviceRestClient.prepareNewFactory();


            final ArrayList<StoredDataMetadata> historyFactoryList = new ArrayList<>(microserviceRestClient.getHistoryFactoryList());
            microserviceRestClient.getHistoryFactory(historyFactoryList.get(0).id);

            ResponseWorkaround<TestVisitor> query1 = microserviceRestClient.query(new TestVisitor());
            TestVisitor query = query1.value;
            Assert.assertEquals("123",query.test);

            Assert.assertEquals(Locale.GERMAN, microserviceRestClient.getLocale());
        } finally {
//            microservice.stop();
        }
    }

    public static class RestClientRoot extends SimpleFactoryBase<Void,Void,RestClientRoot> {
        @Override
        public Void createImpl() {
            return null;
        }
    }


}
