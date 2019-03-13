package de.factoryfx.factory.builder;

import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.server.Microservice;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class DeleteAttributeDanglingIdMigrationTest {

    //----------------------------------old

    public static class ServerFactoryOld extends SimpleFactoryBase<Void,Void, ServerFactoryOld> {




        public final FactoryReferenceAttribute<Void,ServerFactoryNestedOld>  serverFactoryNested1 = new FactoryReferenceAttribute<>(ServerFactoryNestedOld.class);
        public final FactoryReferenceAttribute<Void,ServerFactoryNestedOld>  serverFactoryNested2 = new FactoryReferenceAttribute<>(ServerFactoryNestedOld.class);

        @Override
        public Void createImpl() {
            return null;
        }
    }


    public static class ServerFactoryNestedOld extends SimpleFactoryBase<Void,Void, ServerFactoryOld> {

        public final StringAttribute stringAttribute = new StringAttribute();

        @Override
        public Void createImpl() {
            return null;
        }
    }





    //----------------------------------new


    public static class ServerFactory extends SimpleFactoryBase<Void,Void, ServerFactory> {
        public final FactoryReferenceAttribute<Void, ServerFactoryNested> serverFactoryNested2 = new FactoryReferenceAttribute<>(ServerFactoryNested.class);

        @Override
        public Void createImpl() {
            return null;
        }
    }

    public static class ServerFactoryNested extends SimpleFactoryBase<Void,Void, ServerFactory> {

        public final StringAttribute stringAttribute = new StringAttribute();


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
            FactoryTreeBuilder<Void, Void, ServerFactoryOld, Void> builder = new FactoryTreeBuilder<>(ServerFactoryOld.class);
            builder.addFactory(ServerFactoryOld.class, Scope.SINGLETON, ctx -> {
                ServerFactoryOld serverFactoryOld = new ServerFactoryOld();
                serverFactoryOld.serverFactoryNested1.set(ctx.get(ServerFactoryNestedOld.class));
                serverFactoryOld.serverFactoryNested2.set(ctx.get(ServerFactoryNestedOld.class));
                return serverFactoryOld;
            });
            builder.addFactory(ServerFactoryNestedOld.class, Scope.SINGLETON,  ctx-> {
                ServerFactoryNestedOld serverFactoryNested = new ServerFactoryNestedOld();
                serverFactoryNested.stringAttribute.set("123");
                return serverFactoryNested;
            });
            Microservice<Void, Void, ServerFactoryOld, Void> msOld = builder.microservice().withFilesystemStorage(folder).build();
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
            FactoryTreeBuilder<Void, Void, ServerFactory, Void> builder = new FactoryTreeBuilder<>(ServerFactory.class);
            builder.addFactory(ServerFactory.class, Scope.SINGLETON, ctx -> {
                ServerFactory serverFactory = new ServerFactory();
                serverFactory.serverFactoryNested2.set(ctx.get(ServerFactoryNested.class));
                return serverFactory;
            });
            builder.addFactory(ServerFactoryNested.class, Scope.SINGLETON,  ctx-> {
                ServerFactoryNested serverFactoryNested = new ServerFactoryNested();
                serverFactoryNested.stringAttribute.set("123");
                return serverFactoryNested;
            });
            Microservice<Void, Void, ServerFactory, Void> msNew = builder.microservice().withFilesystemStorage(folder)
                    .withDataMigration((dataMigrationManager) -> {
                        //automatic fix for ids
                       })
                    .build();
            msNew.start();

            ServerFactory serverFactory = msNew.prepareNewFactory().root;

            System.out.println(ObjectMapperBuilder.build().writeValueAsString(serverFactory));
            Assertions.assertEquals("123",serverFactory.serverFactoryNested2.get().stringAttribute.get());
            msNew.stop();
        }
    }



}
