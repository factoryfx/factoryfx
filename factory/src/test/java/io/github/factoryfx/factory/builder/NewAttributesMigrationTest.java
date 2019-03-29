package io.github.factoryfx.factory.builder;

import io.github.factoryfx.factory.attribute.types.StringAttribute;
import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryReferenceAttribute;
import io.github.factoryfx.server.Microservice;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class NewAttributesMigrationTest {

//simplified real work use case
//    this migration was needed after a refactor of Lotus, where we merged two objects into one (Partner and ClientSystem), as they both represented a different aspect of the same concept (a system communicating with Lotus, e.g CAP, SDP...).
//    So after the merge, factories that had an attribute reference on a PartnerFactory, now had an attribute reference on a ClientSystemFactory.
//    Since you can't automatically infer to which ClientSystem the saved PartnerFactory correspond to, the best course of action was to removed the PartnerFactories from the saved config Json and repair the configuration manually once it's deserialized.


    //----------------------------------old

    public static class ServerFactoryOld extends SimpleFactoryBase<Void, ServerFactoryOld> {

        public final FactoryReferenceAttribute<ServerFactoryOld,Void,ClientSystemFactoryOld>  clientSystemFactory1 = new FactoryReferenceAttribute<>();
        public final FactoryReferenceAttribute<ServerFactoryOld,Void,ClientSystemFactoryOld>  clientSystemFactory2 = new FactoryReferenceAttribute<>();

        public final FactoryReferenceAttribute<ServerFactoryOld,Void,ServerFactoryNestedOld>  serverFactoryNested = new FactoryReferenceAttribute<>();

        @Override
        public Void createImpl() {
            return null;
        }
    }


    public static class ServerFactoryNestedOld extends SimpleFactoryBase<Void, ServerFactoryOld> {

        public final FactoryReferenceAttribute<ServerFactoryOld,Void,PartnerFactoryOld>  partnerFactory1 = new FactoryReferenceAttribute<>();

        @Override
        public Void createImpl() {
            return null;
        }
    }


    public static class PartnerFactoryOld extends SimpleFactoryBase<Void, ServerFactoryOld> {
        public final StringAttribute url = new StringAttribute().nullable();


        @Override
        public Void createImpl() {
            return null;
        }
    }

    public static class ClientSystemFactoryOld extends SimpleFactoryBase<Void, ServerFactoryOld> {
        public final StringAttribute url = new StringAttribute().nullable();

        @Override
        public Void createImpl() {
            return null;
        }
    }

    //----------------------------------new


    public static class ServerFactory extends SimpleFactoryBase<Void, ServerFactory> {
        public final FactoryReferenceAttribute<ServerFactory,Void, ClientSystemFactory> clientSystemFactory1 = new FactoryReferenceAttribute<>();
        public final FactoryReferenceAttribute<ServerFactory,Void, ClientSystemFactory> clientSystemFactory2 = new FactoryReferenceAttribute<>();

        public final FactoryReferenceAttribute<ServerFactory,Void, ServerFactoryNested> serverFactoryNested = new FactoryReferenceAttribute<>();

        @Override
        public Void createImpl() {
            return null;
        }
    }

    public static class ServerFactoryNested extends SimpleFactoryBase<Void, ServerFactory> {

        public final FactoryReferenceAttribute<ServerFactory,Void,ClientSystemFactory>  clientSystemFactory = new FactoryReferenceAttribute<>();

        @Override
        public Void createImpl() {
            return null;
        }
    }

    public static class ClientSystemFactory extends SimpleFactoryBase<Void, ServerFactory> {
        public final StringAttribute url = new StringAttribute().nullable();

        @Override
        public Void createImpl() {
            return null;
        }
    }


    @TempDir
    public Path folder;

    @Test
    public void test() throws IOException {
        {
            FactoryTreeBuilder< Void, ServerFactoryOld, Void> builder = new FactoryTreeBuilder<>(ServerFactoryOld.class);
            builder.addFactory(ServerFactoryOld.class, Scope.SINGLETON, ctx -> {
                ServerFactoryOld serverFactoryOld = new ServerFactoryOld();
                serverFactoryOld.clientSystemFactory1.set(ctx.get(ClientSystemFactoryOld.class,"client1"));
                serverFactoryOld.clientSystemFactory2.set(ctx.get(ClientSystemFactoryOld.class,"client2"));
                serverFactoryOld.serverFactoryNested.set(ctx.get(ServerFactoryNestedOld.class));
                return serverFactoryOld;
            });
            builder.addFactory(ClientSystemFactoryOld.class,"client1", Scope.SINGLETON, ctx-> {
                ClientSystemFactoryOld clientSystemFactory=new ClientSystemFactoryOld();
                clientSystemFactory.url.set("client1.de");
                return clientSystemFactory;
            });
            builder.addFactory(ClientSystemFactoryOld.class,"client2", Scope.SINGLETON, ctx-> {
                ClientSystemFactoryOld clientSystemFactory=new ClientSystemFactoryOld();
                clientSystemFactory.url.set("client2.de");
                return clientSystemFactory;
            });
            builder.addFactory(ServerFactoryNestedOld.class, Scope.SINGLETON,  ctx-> {
                ServerFactoryNestedOld serverFactoryNested = new ServerFactoryNestedOld();
                serverFactoryNested.partnerFactory1.set(new PartnerFactoryOld());
                return serverFactoryNested;
            });
            Microservice<Void, ServerFactoryOld, Void> msOld = builder.microservice().withFilesystemStorage(folder).build();
            msOld.start();
            msOld.stop();
        }


        //Patch class names in json files
        String currentFactory=Files.readString(folder.resolve("currentFactory.json"));
        currentFactory=currentFactory.replace("Old","");
        System.out.println(currentFactory);
        Files.writeString(folder.resolve("currentFactory.json"),currentFactory);
        String currentFactorymetadata=Files.readString(folder.resolve("currentFactory_metadata.json"));
        currentFactorymetadata=currentFactorymetadata.replace("Old","");
        System.out.println(currentFactorymetadata);
        Files.writeString(folder.resolve("currentFactory_metadata.json"),currentFactorymetadata);

        {
            FactoryTreeBuilder< Void, ServerFactory, Void> builder = new FactoryTreeBuilder<>(ServerFactory.class);
            builder.addFactory(ServerFactory.class, Scope.SINGLETON, ctx-> {
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

            //no special migartion required just the builder used for migration
            Microservice<Void, ServerFactory, Void> msNew = builder.microservice().withFilesystemStorage(folder).build();
            msNew.start();

            ServerFactory serverFactory = msNew.prepareNewFactory().root;
            Assertions.assertEquals(serverFactory.clientSystemFactory1.get(),serverFactory.serverFactoryNested.get().clientSystemFactory.get());
            msNew.stop();
        }
    }
}
