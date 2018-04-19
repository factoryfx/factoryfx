package de.factoryfx.server;

import de.factoryfx.data.merge.AttributeDiffInfo;
import de.factoryfx.data.merge.MergeDiffInfo;
import de.factoryfx.data.storage.ChangeSummaryCreator;
import de.factoryfx.data.storage.DataAndNewMetadata;
import de.factoryfx.data.storage.StoredDataMetadata;
import de.factoryfx.data.storage.inmemory.InMemoryDataStorage;
import de.factoryfx.factory.AttributeSetupHelper;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.FactoryManager;
import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.factory.builder.FactoryContext;
import de.factoryfx.factory.builder.FactoryTreeBuilder;
import de.factoryfx.factory.builder.Scope;
import de.factoryfx.factory.exception.RethrowingFactoryExceptionHandler;
import de.factoryfx.factory.log.FactoryUpdateLog;
import de.factoryfx.factory.testfactories.ExampleFactoryA;
import de.factoryfx.factory.testfactories.ExampleFactoryB;
import de.factoryfx.factory.testfactories.ExampleLiveObjectA;
import de.factoryfx.factory.testfactories.ExampleLiveObjectB;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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
        ApplicationServer<Void,ExampleFactoryA,Void> applicationServer = new ApplicationServer<Void,ExampleFactoryA,Void>(new FactoryManager<Void,ExampleFactoryA>(new RethrowingFactoryExceptionHandler<>()), memoryFactoryStorage);

        applicationServer.start();

        Assert.assertFalse(root.referenceAttribute.internal_isUserSelectable());
        Assert.assertFalse(root.referenceAttribute.get().referenceAttribute.internal_isUserSelectable());

    }


}