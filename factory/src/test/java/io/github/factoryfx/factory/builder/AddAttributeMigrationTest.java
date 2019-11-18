package io.github.factoryfx.factory.builder;

import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;
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

public class AddAttributeMigrationTest {

    //----------------------------------old
    public static class ServerFactoryOld extends SimpleFactoryBase<Void, ServerFactoryOld> {
        @Override
        protected Void createImpl() {
            return null;
        }
    }


    //----------------------------------new
    public static class ServerFactory extends SimpleFactoryBase<Void, ServerFactory> {
        public final FactoryAttribute<Void, NestedFactory> nestedFactory = new FactoryAttribute<>();

        @Override
        protected Void createImpl() {
            return null;
        }
    }

    public static class NestedFactory extends SimpleFactoryBase<Void, ServerFactory> {
        public final StringAttribute stringAttribute = new StringAttribute();

        @Override
        protected Void createImpl() {
            return null;
        }
    }

    @TempDir
    public Path folder;

    @Test
    public void test_no_conflict() throws IOException {
        {
            FactoryTreeBuilder<Void, ServerFactoryOld> builder = new FactoryTreeBuilder<>(ServerFactoryOld.class, ctx -> new ServerFactoryOld());
            Microservice<Void, ServerFactoryOld> msOld = builder.microservice().withFilesystemStorage(folder).build();
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
            FactoryTreeBuilder<Void, ServerFactory> builder = new FactoryTreeBuilder<>(ServerFactory.class, ctx -> {
                ServerFactory serverFactory = new ServerFactory();
                serverFactory.nestedFactory.set(ctx.get(NestedFactory.class));
                return serverFactory;
            });
            builder.addSingleton(NestedFactory.class, ctx-> {
                NestedFactory serverFactoryNested = new NestedFactory();
                serverFactoryNested.stringAttribute.set("123");
                return serverFactoryNested;
            });
            Microservice<Void, ServerFactory> msNew = builder.microservice().withFilesystemStorage(folder).build();
            msNew.start();

            DataUpdate<ServerFactory> update = msNew.prepareNewFactory();
            update.root.nestedFactory.get().stringAttribute.set("345");
            FactoryUpdateLog<ServerFactory> factoryUpdateLog = msNew.updateCurrentFactory(update);

            Assertions.assertTrue(factoryUpdateLog.mergeDiffInfo.hasNoConflicts());

            msNew.stop();
        }
    }
}
