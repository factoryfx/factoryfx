package de.factoryfx.docu.permission;

import ch.qos.logback.classic.Level;
import de.factoryfx.data.attribute.types.EncryptedStringAttribute;
import de.factoryfx.data.storage.DataAndNewMetadata;
import de.factoryfx.factory.builder.FactoryTreeBuilder;
import de.factoryfx.factory.builder.Scope;
import de.factoryfx.factory.log.FactoryUpdateLog;
import de.factoryfx.jetty.HttpServerConnectorFactory;
import de.factoryfx.microservice.rest.client.MicroserviceRestClient;
import de.factoryfx.microservice.rest.client.MicroserviceRestClientBuilder;
import de.factoryfx.server.Microservice;
import de.factoryfx.server.MicroserviceBuilder;
import de.factoryfx.server.user.persistent.PersistentUserManagementFactory;
import de.factoryfx.server.user.persistent.UserFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

public class Main {

    public static void main(String[] args) {
        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.ERROR);

        UserFactory.passwordKey=new EncryptedStringAttribute().createKey();

        FactoryTreeBuilder<PrinterFactory> builder = new FactoryTreeBuilder<>(PrinterFactory.class);
        builder.addFactory(PrinterFactory.class, Scope.SINGLETON, ctx->{
            PrinterFactory factory = new PrinterFactory();
            factory.text.set("Hello World");
            factory.server.set(ctx.get(PermissionJettyServerFactory.class));
            return factory;
        });
        builder.addFactory(PermissionJettyServerFactory.class, Scope.SINGLETON, ctx->{
            PermissionJettyServerFactory server = new PermissionJettyServerFactory();
            HttpServerConnectorFactory<Void, PrinterFactory> connector = new HttpServerConnectorFactory<>();
            server.connectors.add(connector);
            connector.host.set("localhost");
            connector.port.set(8005);
            server.resource.set(ctx.get(PrinterMicroserviceResourceFactory.class));
            return server;
        }) ;
        builder.addFactory(PrinterMicroserviceResourceFactory.class, Scope.SINGLETON, ctx->{
            PrinterMicroserviceResourceFactory resource = new PrinterMicroserviceResourceFactory();
            PersistentUserManagementFactory<Void, PrinterFactory> userManagement = new PersistentUserManagementFactory<>();
            UserFactory<Void, PrinterFactory> user1 = new UserFactory<>();
            user1.name.set("user1");
            user1.password.setPasswordNotHashed("pw1", UserFactory.passwordKey);
            user1.permissons.add(PrinterFactory.CHANGE_TEXT_PERMISSION);
            user1.locale.set(Locale.ENGLISH);
            userManagement.users.add(user1);
            UserFactory<Void, PrinterFactory> user2 = new UserFactory<>();
            //no Permission for user 2
            user2.name.set("user2");
            user2.password.setPasswordNotHashed("pw2", UserFactory.passwordKey);
            user2.locale.set(Locale.ENGLISH);
            userManagement.users.add(user2);
            resource.userManagement.set(userManagement);

            return resource;
        }) ;



        Microservice<Void, Printer, PrinterFactory, Object> microservice = MicroserviceBuilder.buildInMemoryMicroservice(builder);
        microservice.start();


        System.out.println("first update:");
        {
            MicroserviceRestClient<Void, PrinterFactory, Void> microserviceRestClient = MicroserviceRestClientBuilder.build("localhost",8005,"user1","pw1",PrinterFactory.class);

            DataAndNewMetadata<PrinterFactory> update = microserviceRestClient.prepareNewFactory();
            update.root.text.set("bla blub1");
            FactoryUpdateLog updateLog = microserviceRestClient.updateCurrentFactory(update, "comment");

            System.out.println("PermissionViolations: "+updateLog.mergeDiffInfo.permissionViolations.size());
            microservice.getRootLiveObject().print();//"bla blub1" executed update
        }

        System.out.println("second update:");
        {
            MicroserviceRestClient<Void, PrinterFactory, Void> microserviceRestClient = MicroserviceRestClientBuilder.build("localhost",8005,"user2","pw2",PrinterFactory.class);

            DataAndNewMetadata<PrinterFactory> update = microserviceRestClient.prepareNewFactory();
            update.root.text.set("bla blub2");
            FactoryUpdateLog updateLog =  microserviceRestClient.updateCurrentFactory(update, "comment");

            System.out.println("PermissionViolations: "+updateLog.mergeDiffInfo.permissionViolations.size());
            microservice.getRootLiveObject().print();//"bla blub1" no update
        }




    }
}
