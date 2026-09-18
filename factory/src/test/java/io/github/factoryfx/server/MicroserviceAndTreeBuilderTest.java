package io.github.factoryfx.server;

import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;
import io.github.factoryfx.factory.attribute.primitive.BooleanAttribute;
import io.github.factoryfx.factory.builder.FactoryTreeBuilder;
import io.github.factoryfx.factory.builder.Scope;
import io.github.factoryfx.factory.testfactories.ExampleFactoryA;
import io.github.factoryfx.factory.testfactories.ExampleFactoryB;
import io.github.factoryfx.factory.testfactories.ExampleLiveObjectA;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

public class MicroserviceAndTreeBuilderTest {

    @Test
    public void test_happy_case() {

        FactoryTreeBuilder<ExampleLiveObjectA,ExampleFactoryA> builder = new FactoryTreeBuilder<>(ExampleFactoryA.class, context -> {
            ExampleFactoryA factoryBases = new ExampleFactoryA();
            factoryBases.referenceAttribute.set(context.get(ExampleFactoryB.class));
            return factoryBases;
        });
        builder.addFactory(ExampleFactoryB.class, Scope.PROTOTYPE, context -> {
            ExampleFactoryB factory = new ExampleFactoryB();
            return factory;
        });

        Microservice<ExampleLiveObjectA,ExampleFactoryA> microservice = builder.microservice().build();
        microservice.start();
    }


    public static class RootFactory extends SimpleFactoryBase<String, RootFactory> {
        public final FactoryAttribute<Void, ChildFactory> child = new FactoryAttribute<Void, ChildFactory>().labelText("child").nullable();

        @Override
        protected String createImpl() {
            if (child.get() != null) {
                child.instance();
            }
            return "root";
        }
    }

    public static class ChildFactory extends SimpleFactoryBase<Void, RootFactory> {
        public final BooleanAttribute failOnStart = new BooleanAttribute().labelText("failOnStart");

        @Override
        protected Void createImpl() {
            if (Boolean.TRUE.equals(failOnStart.get())) {
                throw new IllegalStateException("configured start failure");
            }
            return null;
        }
    }

    private FactoryTreeBuilder<String, RootFactory> rootOnlyBuilder() {
        return new FactoryTreeBuilder<>(RootFactory.class, context -> new RootFactory());
    }

    /** a newer version's builder: introduces the child factory into the existing configuration on start */
    private FactoryTreeBuilder<String, RootFactory> builderWithChild(boolean failOnStart) {
        FactoryTreeBuilder<String, RootFactory> builder = new FactoryTreeBuilder<>(RootFactory.class, context -> {
            RootFactory root = new RootFactory();
            root.child.set(context.get(ChildFactory.class));
            return root;
        });
        builder.addFactory(ChildFactory.class, Scope.SINGLETON, context -> {
            ChildFactory child = new ChildFactory();
            child.failOnStart.set(failOnStart);
            return child;
        });
        return builder;
    }

    @Test
    public void rebuildUpdate_persistedAfterSuccessfulStart(@TempDir Path storagePath) {
        Microservice<String, RootFactory> oldVersion = rootOnlyBuilder().microservice().withFilesystemStorage(storagePath).build();
        oldVersion.start();
        oldVersion.stop();

        Microservice<String, RootFactory> newVersion = builderWithChild(false).microservice().withFilesystemStorage(storagePath).build();
        newVersion.start();
        Assertions.assertTrue(newVersion.getHistoryFactoryList(true).stream().anyMatch(metadata -> "FactoryTreeBuilder update".equals(metadata.comment)));
        newVersion.stop();

        //a fresh microservice sees the persisted update: the child factory is part of the stored configuration
        Microservice<String, RootFactory> verify = rootOnlyBuilder().microservice().withFilesystemStorage(storagePath).build();
        verify.start();
        Assertions.assertNotNull(verify.prepareNewFactory().root.child.get(),
                                 "successfully started rebuild update must be persisted");
        verify.stop();
    }

    @Test
    public void rebuildUpdate_notPersistedWhenStartFails(@TempDir Path storagePath) {
        Microservice<String, RootFactory> oldVersion = rootOnlyBuilder().microservice().withFilesystemStorage(storagePath).build();
        oldVersion.start();
        oldVersion.stop();

        Microservice<String, RootFactory> newVersion = builderWithChild(true).microservice().withFilesystemStorage(storagePath).build();
        Assertions.assertThrows(RuntimeException.class, newVersion::start);

        //the previous version still starts: a poisoned stored configuration would contain the failing
        //child factory and block every following start (prepareNewFactory reads the in-memory root,
        //which the merge legitimately mutated - storage is what must stay clean)
        Microservice<String, RootFactory> oldVersionAgain = rootOnlyBuilder().microservice().withFilesystemStorage(storagePath).build();
        oldVersionAgain.start();
        Assertions.assertNull(oldVersionAgain.prepareNewFactory().root.child.get(),
                              "a rebuild producing a non-startable tree must not poison the stored configuration");
        Assertions.assertTrue(oldVersionAgain.getHistoryFactoryList(true).stream().noneMatch(metadata -> "FactoryTreeBuilder update".equals(metadata.comment)));
        oldVersionAgain.stop();
    }
}
