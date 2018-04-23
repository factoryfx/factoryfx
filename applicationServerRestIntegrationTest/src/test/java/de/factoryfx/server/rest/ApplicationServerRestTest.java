package de.factoryfx.server.rest;

import ch.qos.logback.classic.Level;
import de.factoryfx.data.attribute.types.EncryptedString;
import de.factoryfx.data.attribute.types.EncryptedStringAttribute;
import de.factoryfx.data.storage.StoredDataMetadata;
import de.factoryfx.data.storage.inmemory.InMemoryDataStorage;
import de.factoryfx.factory.FactoryManager;
import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.factory.exception.RethrowingFactoryExceptionHandler;
import de.factoryfx.server.ApplicationServer;
import de.factoryfx.server.rest.client.ApplicationServerRestClient;
import de.factoryfx.server.rest.client.ApplicationServerRestClientFactory;
import de.factoryfx.server.rest.client.RestClientFactory;
import de.factoryfx.server.rest.server.HttpServerConnectorFactory;
import de.factoryfx.server.rest.server.JettyServerFactory;
import de.factoryfx.server.user.persistent.PersistentUserManagementFactory;
import de.factoryfx.server.user.persistent.UserFactory;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class ApplicationServerRestTest {

    public static class TestJettyServer extends JettyServerFactory<Void,TestJettyServer>{
        public final FactoryReferenceAttribute<ApplicationServerResource<Void, TestJettyServer,Void>, ApplicationServerResourceFactory<Void, TestJettyServer,Void>> resource = new FactoryReferenceAttribute<>();
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
        final ApplicationServerResourceFactory<Void, TestJettyServer, Void> applicationServerResource = new ApplicationServerResourceFactory<>();
        jettyServer.resource.set(applicationServerResource);
        final PersistentUserManagementFactory<Void,TestJettyServer> userManagement = new PersistentUserManagementFactory<>();
        final UserFactory<Void,TestJettyServer> user = new UserFactory<>();
        user.name.set("user123");
        user.password.set(new EncryptedString("hash123", key));
        user.locale.set(Locale.GERMAN);
        userManagement.users.add(user);
        applicationServerResource.userManagement.set(userManagement);

        jettyServer = jettyServer.utility().prepareUsableCopy();
        ApplicationServer<Void, TestJettyServer, Void> applicationServer = new ApplicationServer<>(new FactoryManager<>(new RethrowingFactoryExceptionHandler()), new InMemoryDataStorage<>(jettyServer));
        Thread serverThread = new Thread(() -> {
            applicationServer.start();
        });
        serverThread.start();

        try {

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }


            ApplicationServerRestClientFactory<Void, RestClientRoot, Void, TestJettyServer> applicationServerRestClientFactory = new ApplicationServerRestClientFactory<>();
            final RestClientFactory<Void,TestJettyServer> restClient = new RestClientFactory<>();
            restClient.port.set(34579);
            restClient.host.set("localhost");
            restClient.path.set("adminui");
            applicationServerRestClientFactory.restClient.set(restClient);
            applicationServerRestClientFactory.user.set("user123");
            applicationServerRestClientFactory.passwordHash.set("hash123");
            applicationServerRestClientFactory.factoryRootClass.set(TestJettyServer.class);

//            applicationServerRestClientFactory=applicationServerRestClientFactory.utility().prepareUsableCopy();

            ApplicationServerRestClient<Void, TestJettyServer> applicationServerRestClient = applicationServerRestClientFactory.internalFactory().instance();
            applicationServerRestClient.prepareNewFactory();


            final ArrayList<StoredDataMetadata> historyFactoryList = new ArrayList<>(applicationServerRestClient.getHistoryFactoryList());
            applicationServerRestClient.getHistoryFactory(historyFactoryList.get(0).id);

            Assert.assertEquals(Locale.GERMAN, applicationServerRestClient.getLocale());
        } finally {
            applicationServer.stop();
        }
    }

    public static class RestClientRoot extends SimpleFactoryBase<Void,Void,RestClientRoot> {
        @Override
        public Void createImpl() {
            return null;
        }
    }


}
