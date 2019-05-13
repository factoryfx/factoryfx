package io.github.factoryfx.factory.builder;


import io.github.factoryfx.factory.storage.migration.metadata.DataStorageMetadataDictionary;
import io.github.factoryfx.factory.testfactories.ExampleFactoryA;
import io.github.factoryfx.factory.testfactories.ExampleFactoryB;
import io.github.factoryfx.factory.testfactories.ExampleLiveObjectA;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FactoryTreeBuilderAttributeFillerTest {

    @Test
    public void test_add(){

        FactoryTreeBuilder<ExampleLiveObjectA, ExampleFactoryA, Void> builder = new FactoryTreeBuilder<>(ExampleFactoryA.class, ctx -> {
            ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
            exampleFactoryA.referenceAttribute.set(ctx.get(ExampleFactoryB.class));
            return exampleFactoryA;
        });
        builder.addFactory(ExampleFactoryB.class, Scope.SINGLETON, ctx-> {
            ExampleFactoryB exampleFactoryB=new ExampleFactoryB();
            exampleFactoryB.stringAttribute.set("111");
            return exampleFactoryB;
        });

        FactoryTreeBuilderAttributeFiller<ExampleLiveObjectA, ExampleFactoryA, Void> filler = new FactoryTreeBuilderAttributeFiller<>(builder);

        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
        exampleFactoryA.internal().finalise();
        DataStorageMetadataDictionary dataStorageMetadataDictionaryFromRoot = exampleFactoryA.internal().createDataStorageMetadataDictionaryFromRoot();
        dataStorageMetadataDictionaryFromRoot.getDataStorageMetadata(ExampleFactoryA.class.getName()).removeAttribute("referenceAttribute");

        ExampleFactoryA exampleFactoryNew = new ExampleFactoryA();
        exampleFactoryNew.internal().finalise();
        Assertions.assertNull(exampleFactoryNew.referenceAttribute.get());
        filler.fillNewAttributes(exampleFactoryNew, dataStorageMetadataDictionaryFromRoot);
        Assertions.assertEquals("111",exampleFactoryNew.referenceAttribute.get().stringAttribute.get());
    }

    @Test
    public void test_dont_override(){
        FactoryTreeBuilder<ExampleLiveObjectA, ExampleFactoryA, Void> builder = new FactoryTreeBuilder<>(ExampleFactoryA.class, ctx -> {
            ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
            exampleFactoryA.referenceAttribute.set(ctx.get(ExampleFactoryB.class));
            return exampleFactoryA;
        });
        builder.addFactory(ExampleFactoryB.class, Scope.SINGLETON, ctx-> {
            ExampleFactoryB exampleFactoryB=new ExampleFactoryB();
            exampleFactoryB.stringAttribute.set("111");
            return exampleFactoryB;
        });

        FactoryTreeBuilderAttributeFiller<ExampleLiveObjectA, ExampleFactoryA, Void> filler = new FactoryTreeBuilderAttributeFiller<>(builder);

        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
        exampleFactoryA.internal().finalise();
        DataStorageMetadataDictionary dataStorageMetadataDictionaryFromRoot = exampleFactoryA.internal().createDataStorageMetadataDictionaryFromRoot();


        ExampleFactoryA exampleFactoryNew = new ExampleFactoryA();
        exampleFactoryNew.internal().finalise();
        Assertions.assertNull(exampleFactoryNew.referenceAttribute.get());
        filler.fillNewAttributes(exampleFactoryNew,dataStorageMetadataDictionaryFromRoot);
        Assertions.assertNull(exampleFactoryNew.referenceAttribute.get());
    }

    @Test
    public void test_add_singleton(){

        FactoryTreeBuilder< ExampleLiveObjectA, ExampleFactoryA, Void> builder = new FactoryTreeBuilder<>(ExampleFactoryA.class, ctx -> {
            ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
            exampleFactoryA.referenceAttribute.set(ctx.get(ExampleFactoryB.class));
            exampleFactoryA.referenceListAttribute.add(ctx.get(ExampleFactoryB.class));
            return exampleFactoryA;
        });
        builder.addFactory(ExampleFactoryB.class, Scope.SINGLETON, ctx-> {
            ExampleFactoryB exampleFactoryB=new ExampleFactoryB();
            exampleFactoryB.stringAttribute.set("111");
            return exampleFactoryB;
        });

        FactoryTreeBuilderAttributeFiller<ExampleLiveObjectA, ExampleFactoryA, Void> filler = new FactoryTreeBuilderAttributeFiller<>(builder);

        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
        exampleFactoryA.internal().finalise();
        DataStorageMetadataDictionary dataStorageMetadataDictionaryFromRoot = exampleFactoryA.internal().createDataStorageMetadataDictionaryFromRoot();
        dataStorageMetadataDictionaryFromRoot.getDataStorageMetadata(ExampleFactoryA.class.getName()).removeAttribute("referenceAttribute");

        ExampleFactoryA exampleFactoryNew = new ExampleFactoryA();
        exampleFactoryNew.referenceAttribute.set(null);
        exampleFactoryNew.referenceListAttribute.add(new ExampleFactoryB());
        exampleFactoryNew.internal().finalise();
        filler.fillNewAttributes(exampleFactoryNew,dataStorageMetadataDictionaryFromRoot);
        Assertions.assertEquals(exampleFactoryNew.referenceAttribute.get(),exampleFactoryNew.referenceListAttribute.get(0));
    }





}
