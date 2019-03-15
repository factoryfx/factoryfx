package de.factoryfx.microservice.test;

import ch.qos.logback.classic.Level;
import de.factoryfx.data.attribute.types.EncryptedStringAttribute;
import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.data.storage.StoredDataMetadata;
import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.factory.builder.FactoryTreeBuilder;
import de.factoryfx.factory.builder.Scope;
import de.factoryfx.jetty.*;
import de.factoryfx.microservice.common.ResponseWorkaround;
import de.factoryfx.microservice.rest.MicroserviceResourceFactory;
import de.factoryfx.microservice.rest.client.MicroserviceRestClient;
import de.factoryfx.microservice.rest.client.MicroserviceRestClientBuilder;
import de.factoryfx.server.Microservice;

import de.factoryfx.server.user.persistent.PersistentUserManagementFactory;
import de.factoryfx.server.user.persistent.UserFactory;
import org.eclipse.jetty.server.Server;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Locale;

public class MicroserviceRestIntegrationTest {

    public static class TestJettyServer  extends SimpleFactoryBase<Server, TestJettyServer> {
        @SuppressWarnings("unchecked")
        public final FactoryReferenceAttribute<Server, JettyServerFactory<TestJettyServer>> server = FactoryReferenceAttribute.create(new FactoryReferenceAttribute<>(JettyServerFactory.class));

        @Override
        public Server createImpl() {
            return server.instance();
        }

        public TestJettyServer(){

        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void integration_test() {
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> e.printStackTrace());

        String key = EncryptedStringAttribute.createKey();
        UserFactory.passwordKey=key;

        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.INFO);

        FactoryTreeBuilder<Server, TestJettyServer, Void> builder = new FactoryTreeBuilder<>(TestJettyServer.class);
        builder.addFactory(TestJettyServer.class, Scope.SINGLETON);
        builder.addFactory(JettyServerFactory.class, Scope.SINGLETON, ctx-> new JettyServerBuilder<>(new JettyServerFactory<TestJettyServer>())
                .withHost("localhost").withPort(34579)
                .withResource(ctx.get(MicroserviceResourceFactory.class)).build());

        builder.addFactory(MicroserviceResourceFactory.class, Scope.SINGLETON, ctx->{
            final MicroserviceResourceFactory<TestJettyServer, Void> microserviceResource = new MicroserviceResourceFactory<>();
            final PersistentUserManagementFactory<TestJettyServer> userManagement = new PersistentUserManagementFactory<>();
            final UserFactory<TestJettyServer> user = new UserFactory<>();
            user.name.set("user123");
            user.password.setPasswordNotHashed("pw1", key);
            user.locale.set(Locale.GERMAN);
            userManagement.users.add(user);
            microserviceResource.userManagement.set(userManagement);
            return  microserviceResource;
        });

        ObjectMapperBuilder.build().copy(builder.buildTree());

        Microservice<Server, TestJettyServer, Void> microservice = builder.microservice().withInMemoryStorage().build();
        microservice.start();

        try {

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            MicroserviceRestClient<TestJettyServer,Void> microserviceRestClient = MicroserviceRestClientBuilder.build("localhost",34579,"user123","pw1",TestJettyServer.class);
            microserviceRestClient.prepareNewFactory();


            final ArrayList<StoredDataMetadata> historyFactoryList = new ArrayList<>(microserviceRestClient.getHistoryFactoryList());
            microserviceRestClient.getHistoryFactory(historyFactoryList.get(0).id);

            Assertions.assertEquals(Locale.GERMAN, microserviceRestClient.getLocale());
        } finally {
            microservice.stop();
        }
    }

    public static class RestClientRoot extends SimpleFactoryBase<Void,RestClientRoot> {
        @Override
        public Void createImpl() {
            return null;
        }
    }


}
