package io.github.factoryfx.factory.builder;

import io.github.factoryfx.factory.BranchSelector;
import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.PolymorphicFactoryBase;
import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;
import io.github.factoryfx.factory.attribute.dependency.FactoryListAttribute;
import io.github.factoryfx.factory.attribute.dependency.FactoryPolymorphicAttribute;
import io.github.factoryfx.factory.attribute.types.StringAttribute;
import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;
import io.github.factoryfx.factory.merge.DataMerger;
import io.github.factoryfx.factory.merge.MergeDiffInfo;
import io.github.factoryfx.factory.storage.migration.metadata.DataStorageMetadataDictionary;
import io.github.factoryfx.factory.testfactories.*;
import io.github.factoryfx.factory.testfactories.poly.ErrorPrinter;
import io.github.factoryfx.factory.testfactories.poly.OutPrinterFactory;
import io.github.factoryfx.factory.testfactories.poly.Printer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class FactoryTreeBuilderReproducibilityTest {

    @Test
    public void test_singleton_root(){
        FactoryTreeBuilder<ExampleLiveObjectA,ExampleFactoryA> factoryTreeBuilder = new FactoryTreeBuilder<>(ExampleFactoryA.class, context -> {
            ExampleFactoryA factory = new ExampleFactoryA();
            return factory;
        });

        ExampleFactoryA root = factoryTreeBuilder.buildTreeUnvalidated();

        ExampleFactoryA rebuildRoot = factoryTreeBuilder.rebuildTreeUnvalidated(root);


        DataMerger<ExampleFactoryA> merge = new DataMerger<>(root,root.internal().copy(),rebuildRoot);

        MergeDiffInfo<ExampleFactoryA> exampleFactoryAMergeDiffInfo = merge.mergeIntoCurrent((p) -> true);
        Assertions.assertEquals(0,exampleFactoryAMergeDiffInfo.mergeInfos.size());
        Assertions.assertEquals(0,exampleFactoryAMergeDiffInfo.conflictInfos.size());
    }

    @Test
    public void test_singleton(){
        FactoryTreeBuilder<ExampleLiveObjectA,ExampleFactoryA> factoryTreeBuilder = new FactoryTreeBuilder<>(ExampleFactoryA.class, ctx -> {
            ExampleFactoryA factory = new ExampleFactoryA();
            factory.referenceAttribute.set(ctx.get(ExampleFactoryB.class));
            return factory;
        });
        factoryTreeBuilder.addFactory(ExampleFactoryB.class,Scope.SINGLETON, ctx -> {
            return new ExampleFactoryB();
        });


        ExampleFactoryA root = factoryTreeBuilder.buildTreeUnvalidated();

        ExampleFactoryA rebuildRoot = factoryTreeBuilder.rebuildTreeUnvalidated(root);


        DataMerger<ExampleFactoryA> merge = new DataMerger<>(root,root.internal().copy(),rebuildRoot);
        MergeDiffInfo<ExampleFactoryA> exampleFactoryAMergeDiffInfo = merge.mergeIntoCurrent((p) -> true);
        Assertions.assertEquals(0,exampleFactoryAMergeDiffInfo.mergeInfos.size());
        Assertions.assertEquals(0,exampleFactoryAMergeDiffInfo.conflictInfos.size());
    }
}