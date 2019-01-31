package de.factoryfx.microservice.test;

import ch.qos.logback.classic.Level;
import de.factoryfx.data.attribute.types.EncryptedStringAttribute;
import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.data.storage.StoredDataMetadata;
import de.factoryfx.data.storage.inmemory.InMemoryDataStorage;
import de.factoryfx.factory.FactoryManager;
import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.factory.builder.FactoryTreeBuilder;
import de.factoryfx.factory.builder.Scope;
import de.factoryfx.factory.exception.RethrowingFactoryExceptionHandler;
import de.factoryfx.jetty.*;
import de.factoryfx.microservice.common.ResponseWorkaround;
import de.factoryfx.microservice.rest.MicroserviceResource;
import de.factoryfx.microservice.rest.MicroserviceResourceFactory;
import de.factoryfx.microservice.rest.client.MicroserviceRestClient;
import de.factoryfx.microservice.rest.client.MicroserviceRestClientBuilder;
import de.factoryfx.microservice.rest.client.MicroserviceRestClientFactory;
import de.factoryfx.server.Microservice;

import de.factoryfx.server.MicroserviceBuilder;
import de.factoryfx.server.user.persistent.PersistentUserManagementFactory;
import de.factoryfx.server.user.persistent.UserFactory;
import org.eclipse.jetty.server.Server;
import org.glassfish.jersey.client.proxy.WebResourceFactory;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MicroserviceRestIntegrationTest {

    public static class TestJettyServer  extends SimpleFactoryBase<Server, TestVisitor, TestJettyServer> {
        @SuppressWarnings("unchecked")
        public final FactoryReferenceAttribute<Server, JettyServerFactory<TestVisitor, TestJettyServer>> server = FactoryReferenceAttribute.create(new FactoryReferenceAttribute<>(JettyServerFactory.class));

        @Override
        public Server createImpl() {
            return server.instance();
        }

        public TestJettyServer(){
            configLifeCycle().setRuntimeQueryExecutor((testVisitor, jettyServer) -> testVisitor.test="123");
        }
    }

    public static class TestVisitor  {
        public String test;
    }

    @SuppressWarnings("unchecked")
    @Test
    public void integration_test() {
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> e.printStackTrace());

        String key = EncryptedStringAttribute.createKey();
        UserFactory.passwordKey=key;

        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.INFO);

        FactoryTreeBuilder<TestJettyServer> builder = new FactoryTreeBuilder<>(TestJettyServer.class);
        builder.addFactory(TestJettyServer.class, Scope.SINGLETON);
        builder.addFactory(JettyServerFactory.class, Scope.SINGLETON, ctx-> new JettyServerBuilder<>(new JettyServerFactory<TestVisitor,TestJettyServer>())
                .withHost("localhost").widthPort(34579)
                .withResource(ctx.get(MicroserviceResourceFactory.class)).build());

        builder.addFactory(MicroserviceResourceFactory.class, Scope.SINGLETON, ctx->{
            final MicroserviceResourceFactory<TestVisitor, TestJettyServer, Void> microserviceResource = new MicroserviceResourceFactory<>();
            final PersistentUserManagementFactory<TestVisitor,TestJettyServer> userManagement = new PersistentUserManagementFactory<>();
            final UserFactory<TestVisitor,TestJettyServer> user = new UserFactory<>();
            user.name.set("user123");
            user.password.setPasswordNotHashed("pw1", key);
            user.locale.set(Locale.GERMAN);
            userManagement.users.add(user);
            microserviceResource.userManagement.set(userManagement);
            return  microserviceResource;
        });

        ObjectMapperBuilder.build().copy(builder.buildTree());

        Microservice<TestVisitor, Server, TestJettyServer, Void> microservice = MicroserviceBuilder.buildInMemoryMicroservice(builder.buildTree());
        microservice.start();

        try {

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            MicroserviceRestClient<TestVisitor, TestJettyServer,Void> microserviceRestClient = MicroserviceRestClientBuilder.build("localhost",34579,"user123","pw1",TestJettyServer.class);
            microserviceRestClient.prepareNewFactory();


            final ArrayList<StoredDataMetadata> historyFactoryList = new ArrayList<>(microserviceRestClient.getHistoryFactoryList());
            microserviceRestClient.getHistoryFactory(historyFactoryList.get(0).id);

            ResponseWorkaround<TestVisitor> query1 = microserviceRestClient.query(new TestVisitor());
            TestVisitor query = query1.value;
            Assert.assertEquals("123",query.test);

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
