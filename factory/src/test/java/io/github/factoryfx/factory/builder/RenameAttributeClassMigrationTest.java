package io.github.factoryfx.factory.builder;

import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.ImmutableValueAttribute;
import io.github.factoryfx.factory.storage.DataUpdate;
import io.github.factoryfx.server.Microservice;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

public class RenameAttributeClassMigrationTest {

    //----------------------------------custom attribute class renamed/moved: LegacyStringAttribute -> CustomStringAttribute, identical serialized form

    public static class LegacyStringAttribute extends ImmutableValueAttribute<String, LegacyStringAttribute> {
    }

    public static class CustomStringAttribute extends ImmutableValueAttribute<String, CustomStringAttribute> {
    }

    public static class ServerFactoryOld extends SimpleFactoryBase<Void, ServerFactoryOld> {
        public final LegacyStringAttribute name = new LegacyStringAttribute().nullable();

        @Override
        protected Void createImpl() {
            return null;
        }
    }

    public static class ServerFactory extends SimpleFactoryBase<Void, ServerFactory> {
        public final CustomStringAttribute name = new CustomStringAttribute().nullable();

        @Override
        protected Void createImpl() {
            return null;
        }
    }

    @TempDir
    public Path folder;

    private void createOldConfiguration(String name) {
        FactoryTreeBuilder<Void, ServerFactoryOld> builderOld = new FactoryTreeBuilder<>(ServerFactoryOld.class, ctx -> {
            ServerFactoryOld factory = new ServerFactoryOld();
            factory.name.set(name);
            return factory;
        });
        Microservice<Void, ServerFactoryOld> microserviceOld = builderOld.microservice().withFilesystemStorage(folder).build();
        microserviceOld.start();
        microserviceOld.stop();

        FileSystemStorageTestUtil.patchClassName(folder);
    }

    private FactoryTreeBuilder<Void, ServerFactory> createNewBuilder() {
        return new FactoryTreeBuilder<>(ServerFactory.class, ctx -> new ServerFactory());
    }

    @Test
    public void test_withoutMigration_valueCleared() {
        createOldConfiguration("hello");

        Microservice<Void, ServerFactory> microservice = createNewBuilder().microservice().withFilesystemStorage(folder).build();
        microservice.start();
        ServerFactory root = microservice.prepareNewFactory().root;
        //stale attribute class name in the stored metadata marks the attribute as retyped, no automatic conversion: value is cleared
        Assertions.assertNull(root.name.get());
        microservice.stop();
    }

    @Test
    public void test_withMigration_valuePreserved() {
        createOldConfiguration("hello");

        Microservice<Void, ServerFactory> microservice = createNewBuilder().microservice().withFilesystemStorage(folder)
                .withRenameAttributeClassMigration(LegacyStringAttribute.class.getName(), CustomStringAttribute.class)
                .build();
        microservice.start();
        ServerFactory root = microservice.prepareNewFactory().root;
        Assertions.assertEquals("hello", root.name.get());
        microservice.stop();
    }

    @Test
    public void test_withMigration_noopAfterResave() {
        createOldConfiguration("hello");

        {
            Microservice<Void, ServerFactory> microservice = createNewBuilder().microservice().withFilesystemStorage(folder)
                    .withRenameAttributeClassMigration(LegacyStringAttribute.class.getName(), CustomStringAttribute.class)
                    .build();
            microservice.start();
            DataUpdate<ServerFactory> update = microservice.prepareNewFactory();
            update.root.name.set("saved");
            microservice.updateCurrentFactory(update);
            microservice.stop();
        }

        //the re-saved metadata records the new attribute class, the still-registered migration is a no-op
        Microservice<Void, ServerFactory> microservice = createNewBuilder().microservice().withFilesystemStorage(folder)
                .withRenameAttributeClassMigration(LegacyStringAttribute.class.getName(), CustomStringAttribute.class)
                .build();
        microservice.start();
        ServerFactory root = microservice.prepareNewFactory().root;
        Assertions.assertEquals("saved", root.name.get());
        microservice.stop();
    }
}
