package io.github.factoryfx.factory.builder;

import io.github.factoryfx.factory.attribute.types.StringAttribute;
import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;
import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;
import io.github.factoryfx.server.Microservice;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class DeleteAttributeDanglingIdMigration2Test {

    //----------------------------------old

    public static class ServerFactoryOld extends SimpleFactoryBase<Void, ServerFactoryOld> {


        public final FactoryAttribute<ServerFactoryOld,Void,ServerFactoryNestedOld> serverFactoryNested = new FactoryAttribute<>();
        public final FactoryAttribute<ServerFactoryOld,Void,ServerFactoryQQQOld> serverFactoryQQQ = new FactoryAttribute<>();

        @Override
        protected Void createImpl() {
            return null;
        }
    }


    public static class ServerFactoryNestedOld extends SimpleFactoryBase<Void, ServerFactoryOld> {

        public final FactoryAttribute<ServerFactoryOld,Void,ServerFactoryQQQOld> serverFactoryQQQ = new FactoryAttribute<>();

        @Override
        protected Void createImpl() {
            return null;
        }
    }

    public static class ServerFactoryQQQOld extends SimpleFactoryBase<Void, ServerFactoryOld> {

        public final StringAttribute stringAttribute = new StringAttribute();

        @Override
        protected Void createImpl() {
            return null;
        }
    }



    //----------------------------------new


    public static class ServerFactory extends SimpleFactoryBase<Void, ServerFactory> {
        public final FactoryAttribute<ServerFactory,Void, ServerFactoryNested> serverFactoryNested = new FactoryAttribute<>();

        @Override
        protected Void createImpl() {
            return null;
        }
    }

    public static class ServerFactoryNested extends SimpleFactoryBase<Void, ServerFactory> {
        public final FactoryAttribute<ServerFactory,Void,ServerFactoryQQQ> serverFactoryQQQ = new FactoryAttribute<>();


        @Override
        protected Void createImpl() {
            return null;
        }
    }

    public static class ServerFactoryQQQ extends SimpleFactoryBase<Void, ServerFactory> {

        public final StringAttribute stringAttribute = new StringAttribute();


        @Override
        protected Void createImpl() {
            return null;
        }
    }

    @TempDir
    public Path folder;

    @Test
    public void test_moreNested() throws IOException {
        {
            FactoryTreeBuilder<Void, ServerFactoryOld> builder = new FactoryTreeBuilder<>(ServerFactoryOld.class, ctx -> {
                ServerFactoryOld serverFactoryOld = new ServerFactoryOld();
                serverFactoryOld.serverFactoryNested.set(ctx.get(ServerFactoryNestedOld.class));
                serverFactoryOld.serverFactoryQQQ.set(ctx.get(ServerFactoryQQQOld.class));
                return serverFactoryOld;
            });
            builder.addFactory(ServerFactoryQQQOld.class, Scope.SINGLETON,  ctx-> {
                ServerFactoryQQQOld serverFactoryNested = new ServerFactoryQQQOld();
                serverFactoryNested.stringAttribute.set("123");
                return serverFactoryNested;
            });
            builder.addFactory(ServerFactoryNestedOld.class, Scope.SINGLETON,  ctx-> {
                ServerFactoryNestedOld serverFactoryNested = new ServerFactoryNestedOld();
                serverFactoryNested.serverFactoryQQQ.set(ctx.get(ServerFactoryQQQOld.class));
                return serverFactoryNested;
            });
            Microservice<Void, ServerFactoryOld> msOld = builder.microservice().withFilesystemStorage(folder).build();
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
            FactoryTreeBuilder<Void, ServerFactory> builder = new FactoryTreeBuilder<>(ServerFactory.class, ctx -> {
                ServerFactory serverFactory = new ServerFactory();
                serverFactory.serverFactoryNested.set(ctx.get(ServerFactoryNested.class));
                return serverFactory;
            });
            builder.addFactory(ServerFactoryNested.class, Scope.SINGLETON,  ctx-> {
                ServerFactoryNested serverFactoryNested = new ServerFactoryNested();
                serverFactoryNested.serverFactoryQQQ.set(ctx.get(ServerFactoryQQQ.class));
                return serverFactoryNested;
            });
            builder.addFactory(ServerFactoryQQQ.class, Scope.SINGLETON,  ctx-> {
                ServerFactoryQQQ serverFactoryQQQ = new ServerFactoryQQQ();
                serverFactoryQQQ.stringAttribute.set("123");
                return serverFactoryQQQ;
            });
            Microservice<Void, ServerFactory> msNew = builder.microservice().withFilesystemStorage(folder).build();
            msNew.start();

            ServerFactory serverFactory = msNew.prepareNewFactory().root;

            System.out.println(ObjectMapperBuilder.build().writeValueAsString(serverFactory));
            Assertions.assertEquals("123",serverFactory.serverFactoryNested.get().serverFactoryQQQ.get().stringAttribute.get());
            msNew.stop();
        }
    }



}
