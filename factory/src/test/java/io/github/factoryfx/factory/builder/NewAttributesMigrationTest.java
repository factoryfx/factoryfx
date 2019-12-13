package io.github.factoryfx.factory.builder;

import io.github.factoryfx.factory.attribute.types.StringAttribute;
import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;
import io.github.factoryfx.server.Microservice;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

public class NewAttributesMigrationTest {

//simplified real work use case
//    this migration was needed after a refactor of Lotus, where we merged two objects into one (Partner and ClientSystem), as they both represented a different aspect of the same concept (a system communicating with Lotus, e.g CAP, SDP...).
//    So after the merge, factories that had an attribute reference on a PartnerFactory, now had an attribute reference on a ClientSystemFactory.
//    Since you can't automatically infer to which ClientSystem the saved PartnerFactory correspond to, the best course of action was to removed the PartnerFactories from the saved config Json and repair the configuration manually once it's deserialized.


    //----------------------------------old

    public static class ServerFactoryOld extends SimpleFactoryBase<Void, ServerFactoryOld> {

        public final FactoryAttribute<Void,ClientSystemFactoryOld> clientSystemFactory1 = new FactoryAttribute<>();
        public final FactoryAttribute<Void,ClientSystemFactoryOld> clientSystemFactory2 = new FactoryAttribute<>();

        public final FactoryAttribute<Void,ServerFactoryNestedOld> serverFactoryNested = new FactoryAttribute<>();

        @Override
        protected Void createImpl() {
            return null;
        }
    }


    public static class ServerFactoryNestedOld extends SimpleFactoryBase<Void, ServerFactoryOld> {

        public final FactoryAttribute<Void,PartnerFactoryOld> partnerFactory1 = new FactoryAttribute<>();

        @Override
        protected Void createImpl() {
            return null;
        }
    }


    public static class PartnerFactoryOld extends SimpleFactoryBase<Void, ServerFactoryOld> {
        public final StringAttribute url = new StringAttribute().nullable();


        @Override
        protected Void createImpl() {
            return null;
        }
    }

    public static class ClientSystemFactoryOld extends SimpleFactoryBase<Void, ServerFactoryOld> {
        public final StringAttribute url = new StringAttribute().nullable();

        @Override
        protected Void createImpl() {
            return null;
        }
    }

    //----------------------------------new


    public static class ServerFactory extends SimpleFactoryBase<Void, ServerFactory> {
        public final FactoryAttribute<Void, ClientSystemFactory> clientSystemFactory1 = new FactoryAttribute<>();
        public final FactoryAttribute<Void, ClientSystemFactory> clientSystemFactory2 = new FactoryAttribute<>();

        public final FactoryAttribute<Void, ServerFactoryNested> serverFactoryNested = new FactoryAttribute<>();

        @Override
        protected Void createImpl() {
            return null;
        }
    }

    public static class ServerFactoryNested extends SimpleFactoryBase<Void, ServerFactory> {

        public final FactoryAttribute<Void,ClientSystemFactory> clientSystemFactory = new FactoryAttribute<>();

        @Override
        protected Void createImpl() {
            return null;
        }
    }

    public static class ClientSystemFactory extends SimpleFactoryBase<Void, ServerFactory> {
        public final StringAttribute url = new StringAttribute().nullable();

        @Override
        protected Void createImpl() {
            return null;
        }
    }


    @TempDir
    public Path folder;

    @Test
    public void test() {
        {
            FactoryTreeBuilder< Void, ServerFactoryOld> builder = new FactoryTreeBuilder<>(ServerFactoryOld.class, ctx -> {
                ServerFactoryOld serverFactoryOld = new ServerFactoryOld();
                serverFactoryOld.clientSystemFactory1.set(ctx.get(ClientSystemFactoryOld.class,"client1"));
                serverFactoryOld.clientSystemFactory2.set(ctx.get(ClientSystemFactoryOld.class,"client2"));
                serverFactoryOld.serverFactoryNested.set(ctx.get(ServerFactoryNestedOld.class));
                return serverFactoryOld;
            });
            builder.addSingleton(ClientSystemFactoryOld.class,"client1", ctx-> {
                ClientSystemFactoryOld clientSystemFactory=new ClientSystemFactoryOld();
                clientSystemFactory.url.set("client1.de");
                return clientSystemFactory;
            });
            builder.addSingleton(ClientSystemFactoryOld.class,"client2", ctx-> {
                ClientSystemFactoryOld clientSystemFactory=new ClientSystemFactoryOld();
                clientSystemFactory.url.set("client2.de");
                return clientSystemFactory;
            });
            builder.addSingleton(ServerFactoryNestedOld.class,  ctx-> {
                ServerFactoryNestedOld serverFactoryNested = new ServerFactoryNestedOld();
                serverFactoryNested.partnerFactory1.set(ctx.get(PartnerFactoryOld.class));
                return serverFactoryNested;
            });
            builder.addSingleton(PartnerFactoryOld.class,  ctx-> {
                return new PartnerFactoryOld();
            });
            Microservice<Void, ServerFactoryOld> msOld = builder.microservice().withFilesystemStorage(folder).build();
            msOld.start();
            msOld.stop();
        }

        FileSystemStorageTestUtil.patchClassName(folder);

        {
            FactoryTreeBuilder< Void, ServerFactory> builder = new FactoryTreeBuilder<>(ServerFactory.class, ctx-> {
                ServerFactory serverFactory = new ServerFactory();
                serverFactory.clientSystemFactory1.set(ctx.get(ClientSystemFactory.class,"client1"));
                serverFactory.clientSystemFactory2.set(ctx.get(ClientSystemFactory.class,"client2"));
                serverFactory.serverFactoryNested.set(ctx.get(ServerFactoryNested.class));
                return serverFactory;
            });

            builder.addFactory(ClientSystemFactory.class,"client1", Scope.SINGLETON, ctx-> {
                ClientSystemFactory clientSystemFactory=new ClientSystemFactory();
                clientSystemFactory.url.set("client1.de");
                return clientSystemFactory;
            });
            builder.addFactory(ClientSystemFactory.class,"client2", Scope.SINGLETON, ctx-> {
                ClientSystemFactory clientSystemFactory=new ClientSystemFactory();
                clientSystemFactory.url.set("client2.de");
                return clientSystemFactory;
            });
            builder.addFactory(ServerFactoryNested.class, Scope.SINGLETON,  ctx-> {
                ServerFactoryNested serverFactoryNested = new ServerFactoryNested();
                serverFactoryNested.clientSystemFactory.set(ctx.get(ClientSystemFactory.class,"client1"));
                return serverFactoryNested;
            });

            //no special migration required just the builder used for migration
            Microservice<Void, ServerFactory> msNew = builder.microservice().withFilesystemStorage(folder).build();
            msNew.start();

            ServerFactory serverFactory = msNew.prepareNewFactory().root;
            Assertions.assertEquals(serverFactory.clientSystemFactory1.get(),serverFactory.serverFactoryNested.get().clientSystemFactory.get());
            msNew.stop();
        }
    }
}
