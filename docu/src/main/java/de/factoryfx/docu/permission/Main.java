package de.factoryfx.docu.permission;

import ch.qos.logback.classic.Level;
import de.factoryfx.data.attribute.types.EncryptedStringAttribute;
import de.factoryfx.data.storage.DataAndStoredMetadata;
import de.factoryfx.factory.builder.FactoryTreeBuilder;
import de.factoryfx.factory.builder.Scope;
import de.factoryfx.factory.log.FactoryUpdateLog;
import de.factoryfx.jetty.JettyServerBuilder;
import de.factoryfx.jetty.JettyServerFactory;
import de.factoryfx.microservice.rest.client.MicroserviceRestClient;
import de.factoryfx.microservice.rest.client.MicroserviceRestClientBuilder;
import de.factoryfx.server.Microservice;
import de.factoryfx.server.user.persistent.PersistentUserManagementFactory;
import de.factoryfx.server.user.persistent.UserFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

public class Main {
    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.ERROR);

        UserFactory.passwordKey=EncryptedStringAttribute.createKey();

        FactoryTreeBuilder<Void, Printer, PrinterFactory, Void> builder = new FactoryTreeBuilder<>(PrinterFactory.class);
        builder.addFactory(PrinterFactory.class, Scope.SINGLETON, ctx->{
            PrinterFactory factory = new PrinterFactory();
            factory.text.set("Hello World");
            factory.server.set(ctx.get(JettyServerFactory.class));
            return factory;
        });
        builder.addFactory(JettyServerFactory.class, Scope.SINGLETON, ctx-> new JettyServerBuilder<>(new JettyServerFactory<Void,PrinterFactory>())
                .withHost("localhost").widthPort(8005)
                .withResource(ctx.get(PrinterMicroserviceResourceFactory.class)).build());
        builder.addFactory(PrinterMicroserviceResourceFactory.class, Scope.SINGLETON, ctx->{
            PrinterMicroserviceResourceFactory resource = new PrinterMicroserviceResourceFactory();
            PersistentUserManagementFactory<Void, PrinterFactory> userManagement = new PersistentUserManagementFactory<>();
            UserFactory<Void, PrinterFactory> user1 = new UserFactory<>();
            user1.name.set("user1");
            user1.password.setPasswordNotHashed("pw1", UserFactory.passwordKey);
            user1.permissions.add(PrinterFactory.CHANGE_TEXT_PERMISSION);
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

        Microservice<Void, Printer, PrinterFactory, Void> microservice = builder.microservice().withInMemoryStorage().build();
        microservice.start();

        System.out.println("first update:");
        {
            MicroserviceRestClient<Void, PrinterFactory, Void> microserviceRestClient = MicroserviceRestClientBuilder.build("localhost",8005,"user1","pw1",PrinterFactory.class);

            DataAndStoredMetadata<PrinterFactory, Void> update = microserviceRestClient.prepareNewFactory();
            update.root.text.set("bla blub1");
            FactoryUpdateLog updateLog = microserviceRestClient.updateCurrentFactory(update, "comment");

            System.out.println("PermissionViolations: "+updateLog.mergeDiffInfo.permissionViolations.size());
            microservice.getRootLiveObject().print();//"bla blub1" executed update
        }

        System.out.println("second update:");
        {
            MicroserviceRestClient<Void, PrinterFactory, Void> microserviceRestClient = MicroserviceRestClientBuilder.build("localhost",8005,"user2","pw2",PrinterFactory.class);

            DataAndStoredMetadata<PrinterFactory, Void> update = microserviceRestClient.prepareNewFactory();
            update.root.text.set("bla blub2");
            FactoryUpdateLog updateLog =  microserviceRestClient.updateCurrentFactory(update, "comment");

            System.out.println("PermissionViolations: "+updateLog.mergeDiffInfo.permissionViolations.size());
            microservice.getRootLiveObject().print();//"bla blub1" no update
        }




    }
}
