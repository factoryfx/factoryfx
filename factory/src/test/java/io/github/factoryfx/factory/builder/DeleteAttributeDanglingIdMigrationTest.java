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

public class DeleteAttributeDanglingIdMigrationTest {

    //----------------------------------old
    public static class ServerFactoryOld extends SimpleFactoryBase<Void, ServerFactoryOld> {

        public final FactoryAttribute<Void,ServerFactoryNestedOld> serverFactoryNested1 = new FactoryAttribute<>();
        public final FactoryAttribute<Void,ServerFactoryNestedOld> serverFactoryNested2 = new FactoryAttribute<>();

        @Override
        protected Void createImpl() {
            return null;
        }
    }


    public static class ServerFactoryNestedOld extends SimpleFactoryBase<Void, ServerFactoryOld> {

        public final StringAttribute stringAttribute = new StringAttribute();

        @Override
        protected Void createImpl() {
            return null;
        }
    }

    //----------------------------------new
    public static class ServerFactory extends SimpleFactoryBase<Void, ServerFactory> {
        public final FactoryAttribute<Void, ServerFactoryNested> serverFactoryNested2 = new FactoryAttribute<>();

        @Override
        protected Void createImpl() {
            return null;
        }
    }

    public static class ServerFactoryNested extends SimpleFactoryBase<Void, ServerFactory> {

        public final StringAttribute stringAttribute = new StringAttribute();


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
            FactoryTreeBuilder<Void, ServerFactoryOld> builder = new FactoryTreeBuilder<>(ServerFactoryOld.class, ctx -> {
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
            Microservice<Void, ServerFactoryOld> msOld = builder.microservice().withFilesystemStorage(folder).build();
            msOld.start();
            msOld.stop();
        }

        FileSystemStorageTestUtil.patchClassName(folder);

        {
            FactoryTreeBuilder<Void, ServerFactory> builder = new FactoryTreeBuilder<>(ServerFactory.class, ctx -> {
                ServerFactory serverFactory = new ServerFactory();
                serverFactory.serverFactoryNested2.set(ctx.get(ServerFactoryNested.class));
                return serverFactory;
            });
            builder.addFactory(ServerFactoryNested.class, Scope.SINGLETON,  ctx-> {
                ServerFactoryNested serverFactoryNested = new ServerFactoryNested();
                serverFactoryNested.stringAttribute.set("123");
                return serverFactoryNested;
            });
            Microservice<Void, ServerFactory> msNew = builder.microservice().withFilesystemStorage(folder).build();
            msNew.start();

            ServerFactory serverFactory = msNew.prepareNewFactory().root;

            System.out.println(ObjectMapperBuilder.build().writeValueAsString(serverFactory));
            Assertions.assertEquals("123",serverFactory.serverFactoryNested2.get().stringAttribute.get());
            msNew.stop();
        }
    }



}
