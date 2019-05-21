package io.github.factoryfx.factory.builder;

import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;
import io.github.factoryfx.factory.attribute.dependency.FactoryViewAttribute;
import io.github.factoryfx.factory.attribute.types.StringAttribute;
import io.github.factoryfx.factory.log.FactoryUpdateLog;
import io.github.factoryfx.factory.storage.DataUpdate;
import io.github.factoryfx.server.Microservice;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class AddFactoryViewAttributeMigrationTest {

    //----------------------------------old
    public static class ServerFactoryOld extends SimpleFactoryBase<Void, ServerFactoryOld> {

        public final FactoryAttribute<ServerFactoryOld, Void, NestedFactoryOld> nestedFactory = new FactoryAttribute<>();
        public final FactoryAttribute<ServerFactoryOld, Void, ViewFactoryOld> view = new FactoryAttribute<>();

        @Override
        public Void createImpl() {
            return null;
        }
    }


    public static class ViewFactoryOld extends SimpleFactoryBase<Void, ServerFactoryOld> {

        public final StringAttribute stringAttribute = new StringAttribute();

        @Override
        public Void createImpl() {
            return null;
        }
    }

    public static class NestedFactoryOld extends SimpleFactoryBase<Void, ServerFactoryOld> {
        @Override
        public Void createImpl() {
            return null;
        }
    }


    //----------------------------------new
    public static class ServerFactory extends SimpleFactoryBase<Void, ServerFactory> {
        public final FactoryAttribute<ServerFactory,Void, NestedFactory> nestedFactory = new FactoryAttribute<>();
        public final FactoryAttribute<ServerFactory, Void, ViewFactory> view = new FactoryAttribute<>();

        @Override
        public Void createImpl() {
            return null;
        }
    }

    public static class NestedFactory extends SimpleFactoryBase<Void, ServerFactory> {

        public final FactoryViewAttribute<ServerFactory, Void, ViewFactory> view = new FactoryViewAttribute<>(s -> s.view.get());

        @Override
        public Void createImpl() {
            return null;
        }
    }

    public static class ViewFactory extends SimpleFactoryBase<Void, ServerFactory> {

        public final StringAttribute stringAttribute = new StringAttribute();

        @Override
        public Void createImpl() {
            return null;
        }
    }

    @TempDir
    public Path folder;

    @Test
    public void test_no_null_pointer_exception() throws IOException {
        {
            FactoryTreeBuilder<Void, ServerFactoryOld, Void> builder = new FactoryTreeBuilder<>(ServerFactoryOld.class, ctx -> {
                ServerFactoryOld serverFactory = new ServerFactoryOld();
                serverFactory.nestedFactory.set(ctx.get(NestedFactoryOld.class));
                serverFactory.view.set(ctx.get(ViewFactoryOld.class));
                return serverFactory;
            });

            builder.addFactory(NestedFactoryOld.class, Scope.SINGLETON, ctx -> new NestedFactoryOld());
            builder.addFactory(ViewFactoryOld.class, Scope.SINGLETON, ctx -> {
                ViewFactoryOld viewFactory = new ViewFactoryOld();
                viewFactory.stringAttribute.set("string value");
                return viewFactory;
            });


            Microservice<Void, ServerFactoryOld, Void> msOld = builder.microservice().withFilesystemStorage(folder).build();
            msOld.start();
            msOld.stop();
        }

        //Patch class names in json files
        String currentFactory=Files.readString(folder.resolve("currentFactory.json"));
        currentFactory=currentFactory.replace("Old","");
        Files.writeString(folder.resolve("currentFactory.json"),currentFactory);
        String currentFactorymetadata=Files.readString(folder.resolve("currentFactory_metadata.json"));
        currentFactorymetadata=currentFactorymetadata.replace("Old","");
        Files.writeString(folder.resolve("currentFactory_metadata.json"),currentFactorymetadata);
        for (File file : folder.resolve("history").toFile().listFiles()) {
            String historyFactory=Files.readString(file.toPath());
            historyFactory=historyFactory.replace("Old","");
            Files.writeString(file.toPath(),historyFactory);
        }



        {
            FactoryTreeBuilder<Void, ServerFactory, Void> builder = new FactoryTreeBuilder<>(ServerFactory.class, ctx -> {
                ServerFactory serverFactory = new ServerFactory();
                serverFactory.nestedFactory.set(ctx.get(NestedFactory.class));
                serverFactory.view.set(ctx.get(ViewFactory.class));
                return serverFactory;
            });

            builder.addFactory(NestedFactory.class, Scope.SINGLETON, ctx -> new NestedFactory());
            builder.addFactory(ViewFactory.class, Scope.SINGLETON, ctx -> {
                ViewFactory viewFactory = new ViewFactory();
                viewFactory.stringAttribute.set("string value");
                return viewFactory;
            });

            Microservice<Void, ServerFactory, Void> msNew = builder.microservice().withFilesystemStorage(folder).build();
            msNew.start();

            DataUpdate<ServerFactory> update = msNew.prepareNewFactory();
            msNew.updateCurrentFactory(update);

            msNew.stop();
        }
    }
}
