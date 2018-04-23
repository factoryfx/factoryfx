package de.factoryfx.server;

import de.factoryfx.data.storage.inmemory.InMemoryDataStorage;
import de.factoryfx.factory.FactoryManager;
import de.factoryfx.factory.builder.FactoryTreeBuilder;
import de.factoryfx.factory.builder.Scope;
import de.factoryfx.factory.exception.RethrowingFactoryExceptionHandler;
import de.factoryfx.factory.testfactories.ExampleFactoryA;
import de.factoryfx.factory.testfactories.ExampleFactoryB;
import org.junit.Assert;
import org.junit.Test;

public class ApplicationServerAndTreeBuilderTest {




    @Test
    public void test_happy_case() {

        FactoryTreeBuilder<ExampleFactoryA> builder = new FactoryTreeBuilder<>(ExampleFactoryA.class);
        builder.addFactory(ExampleFactoryA.class, Scope.SINGLETON, context -> {
            ExampleFactoryA factoryBases = new ExampleFactoryA();
            factoryBases.referenceAttribute.set(context.get(ExampleFactoryB.class));
            return factoryBases;
        });
        builder.addFactory(ExampleFactoryB.class, Scope.PROTOTYPE, context -> {
            ExampleFactoryB factoryBases = new ExampleFactoryB();
            return factoryBases;
        });


        ExampleFactoryA root = builder.buildTreeUnvalidated();
        final InMemoryDataStorage<ExampleFactoryA, Void> memoryFactoryStorage = new InMemoryDataStorage<>(root);
        ApplicationServer<Void,ExampleFactoryA,Void> applicationServer = new ApplicationServer<>(new FactoryManager<>(new RethrowingFactoryExceptionHandler()), memoryFactoryStorage);

        applicationServer.start();

        Assert.assertFalse(root.referenceAttribute.internal_isUserSelectable());
        Assert.assertFalse(root.referenceAttribute.get().referenceAttribute.internal_isUserSelectable());

    }


}