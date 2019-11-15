package io.github.factoryfx.docu.permission;

import ch.qos.logback.classic.Level;
import io.github.factoryfx.factory.attribute.types.EncryptedStringAttribute;
import io.github.factoryfx.factory.storage.DataUpdate;
import io.github.factoryfx.factory.builder.FactoryTreeBuilder;
import io.github.factoryfx.factory.builder.Scope;
import io.github.factoryfx.factory.log.FactoryUpdateLog;
import io.github.factoryfx.jetty.JettyServerFactory;
import io.github.factoryfx.jetty.builder.SimpleJettyServerBuilder;
import io.github.factoryfx.microservice.rest.client.MicroserviceRestClient;
import io.github.factoryfx.microservice.rest.client.MicroserviceRestClientBuilder;
import io.github.factoryfx.server.Microservice;
import io.github.factoryfx.server.user.persistent.PersistentUserManagementFactory;
import io.github.factoryfx.server.user.persistent.UserFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

public class Main {
    public static void main(String[] args) {
        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.ERROR);

        UserFactory.passwordKey= EncryptedStringAttribute.createKey();

        FactoryTreeBuilder< Printer, PrinterFactory> builder = new FactoryTreeBuilder<>(PrinterFactory.class, ctx->{
            PrinterFactory factory = new PrinterFactory();
            factory.text.set("Hello World");
            factory.server.set(ctx.getUnsafe(JettyServerFactory.class));
            return factory;
        });
        builder.addBuilder(ctx->
            new SimpleJettyServerBuilder<PrinterFactory>()
                    .withHost("localhost").withPort(8005)
                    .withResource(ctx.get(PrinterMicroserviceResourceFactory.class))
        );
        builder.addSingleton(PrinterMicroserviceResourceFactory.class, ctx->{
            PrinterMicroserviceResourceFactory resource = new PrinterMicroserviceResourceFactory();
            PersistentUserManagementFactory<PrinterFactory> userManagementFactory = ctx.getUnsafe(PersistentUserManagementFactory.class);
            resource.userManagement.set(userManagementFactory);
            return resource;
        });
        builder.addFactoryUnsafe(PersistentUserManagementFactory.class, Scope.SINGLETON,ctx->{
            PersistentUserManagementFactory<PrinterFactory> userManagement = new PersistentUserManagementFactory<>();
            userManagement.users.add(ctx.getUnsafe(UserFactory.class,"user1"));
            userManagement.users.add(ctx.getUnsafe(UserFactory.class,"user2"));
            return userManagement;
        });
        builder.addFactoryUnsafe(UserFactory.class,"user1",Scope.SINGLETON, ctx->{
            UserFactory<PrinterFactory> user1 = new UserFactory<>();
            user1.name.set("user1");
            user1.password.setPasswordNotHashed("pw1", UserFactory.passwordKey);
            user1.permissions.add(PrinterFactory.CHANGE_TEXT_PERMISSION);
            user1.locale.set(Locale.ENGLISH);
            return user1;
        });
        builder.addFactoryUnsafe(UserFactory.class,"user2",Scope.SINGLETON, ctx->{
            UserFactory<PrinterFactory> user2 = new UserFactory<>();
            //no Permission for user 2
            user2.name.set("user2");
            user2.password.setPasswordNotHashed("pw2", UserFactory.passwordKey);
            user2.locale.set(Locale.ENGLISH);
            return user2;
        });



        Microservice<Printer, PrinterFactory> microservice = builder.microservice().build();
        microservice.start();

        System.out.println("first update:");
        {
            MicroserviceRestClient<PrinterFactory> microserviceRestClient = MicroserviceRestClientBuilder.build("localhost",8005,"user1","pw1",PrinterFactory.class);

            DataUpdate<PrinterFactory> update = microserviceRestClient.prepareNewFactory();
            update.root.text.set("bla blub1");
            FactoryUpdateLog updateLog = microserviceRestClient.updateCurrentFactory(update, "comment");

            System.out.println("PermissionViolations: "+updateLog.mergeDiffInfo.permissionViolations.size());
            microservice.getRootLiveObject().print();//"bla blub1" executed update
        }

        System.out.println("second update:");
        {
            MicroserviceRestClient<PrinterFactory> microserviceRestClient = MicroserviceRestClientBuilder.build("localhost",8005,"user2","pw2",PrinterFactory.class);

            DataUpdate<PrinterFactory> update = microserviceRestClient.prepareNewFactory();
            update.root.text.set("bla blub2");
            FactoryUpdateLog updateLog =  microserviceRestClient.updateCurrentFactory(update, "comment");

            System.out.println("PermissionViolations: "+updateLog.mergeDiffInfo.permissionViolations.size());
            microservice.getRootLiveObject().print();//"bla blub1" no update
        }




    }
}
