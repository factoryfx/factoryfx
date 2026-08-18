package io.github.factoryfx.factory.builder;

import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryListAttribute;
import io.github.factoryfx.factory.attribute.types.StringAttribute;
import io.github.factoryfx.factory.attribute.types.StringListAttribute;
import io.github.factoryfx.server.Microservice;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

/**
 * regression test: renaming a (list) attribute must not drop the stored data
 */
public class ListAttributeRenameMigrationTest {

    //----------------------------------old

    public static class ServerFactoryOld extends SimpleFactoryBase<Void, ServerFactoryOld> {
        public final StringListAttribute values = new StringListAttribute();
        public final FactoryListAttribute<Void, ChildFactoryOld> children = new FactoryListAttribute<>();

        @Override
        protected Void createImpl() {
            return null;
        }
    }

    public static class ChildFactoryOld extends SimpleFactoryBase<Void, ServerFactoryOld> {
        public final StringAttribute name = new StringAttribute();

        @Override
        protected Void createImpl() {
            return null;
        }
    }

    //----------------------------------new: values renamed to renamedValues, children renamed to renamedChildren

    public static class ServerFactory extends SimpleFactoryBase<Void, ServerFactory> {
        public final StringListAttribute renamedValues = new StringListAttribute();
        public final FactoryListAttribute<Void, ChildFactory> renamedChildren = new FactoryListAttribute<>();

        @Override
        protected Void createImpl() {
            return null;
        }
    }

    public static class ChildFactory extends SimpleFactoryBase<Void, ServerFactory> {
        public final StringAttribute name = new StringAttribute();

        @Override
        protected Void createImpl() {
            return null;
        }
    }

    @TempDir
    public Path folder;

    @Test
    public void test_renameListAttributes_dataPreserved() {
        {
            FactoryTreeBuilder<Void, ServerFactoryOld> builderOld = new FactoryTreeBuilder<>(ServerFactoryOld.class, ctx -> {
                ServerFactoryOld factory = new ServerFactoryOld();
                factory.values.set(List.of("a", "b", "c"));
                ChildFactoryOld child1 = new ChildFactoryOld();
                child1.name.set("child1");
                ChildFactoryOld child2 = new ChildFactoryOld();
                child2.name.set("child2");
                factory.children.add(child1);
                factory.children.add(child2);
                return factory;
            });
            builderOld.markAsNonPersistentFactoryBuilder();
            Microservice<Void, ServerFactoryOld> microserviceOld = builderOld.microservice().withFilesystemStorage(folder).build();
            microserviceOld.start();
            microserviceOld.stop();
        }

        FileSystemStorageTestUtil.patchClassName(folder);

        {
            FactoryTreeBuilder<Void, ServerFactory> builder = new FactoryTreeBuilder<>(ServerFactory.class, ctx -> new ServerFactory());
            Microservice<Void, ServerFactory> microservice = builder.microservice().withFilesystemStorage(folder)
                    .withRenameAttributeMigration(ServerFactory.class, "values", (f) -> f.renamedValues)
                    .withRenameAttributeMigration(ServerFactory.class, "children", (f) -> f.renamedChildren)
                    .build();
            microservice.start();
            ServerFactory root = microservice.prepareNewFactory().root;
            Assertions.assertEquals(List.of("a", "b", "c"), root.renamedValues.get());
            Assertions.assertEquals(2, root.renamedChildren.size());
            Assertions.assertEquals("child1", root.renamedChildren.get(0).name.get());
            Assertions.assertEquals("child2", root.renamedChildren.get(1).name.get());
            microservice.stop();
        }
    }
}
