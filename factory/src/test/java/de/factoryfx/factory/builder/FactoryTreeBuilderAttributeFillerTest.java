package de.factoryfx.factory.builder;


import de.factoryfx.data.storage.migration.metadata.DataStorageMetadataDictionary;
import de.factoryfx.factory.testfactories.ExampleFactoryA;
import de.factoryfx.factory.testfactories.ExampleFactoryB;
import de.factoryfx.factory.testfactories.ExampleLiveObjectA;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FactoryTreeBuilderAttributeFillerTest {

    @Test
    public void test_add(){

        FactoryTreeBuilder<Void, ExampleLiveObjectA, ExampleFactoryA, Void> builder = new FactoryTreeBuilder<>(ExampleFactoryA.class);
        builder.addFactory(ExampleFactoryA.class, Scope.SINGLETON, ctx -> {
            ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
            exampleFactoryA.referenceAttribute.set(ctx.get(ExampleFactoryB.class));
            return exampleFactoryA;
        });
        builder.addFactory(ExampleFactoryB.class, Scope.SINGLETON, ctx-> {
            ExampleFactoryB exampleFactoryB=new ExampleFactoryB();
            exampleFactoryB.stringAttribute.set("111");
            return exampleFactoryB;
        });

        FactoryTreeBuilderAttributeFiller<Void, ExampleLiveObjectA, ExampleFactoryA, Void> filler = new FactoryTreeBuilderAttributeFiller<>(builder);

        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
        exampleFactoryA.internal().addBackReferences();
        DataStorageMetadataDictionary dataStorageMetadataDictionaryFromRoot = exampleFactoryA.internal().createDataStorageMetadataDictionaryFromRoot();
        dataStorageMetadataDictionaryFromRoot.getDataStorageMetadata(ExampleFactoryA.class.getName()).removeAttribute("referenceAttribute");

        ExampleFactoryA exampleFactoryNew = new ExampleFactoryA();
        exampleFactoryNew.internal().addBackReferences();
        Assertions.assertNull(exampleFactoryNew.referenceAttribute.get());
        filler.fillNewAttributes(exampleFactoryNew, dataStorageMetadataDictionaryFromRoot);
        Assertions.assertEquals("111",exampleFactoryNew.referenceAttribute.get().stringAttribute.get());
    }

    @Test
    public void test_dont_override(){
        FactoryTreeBuilder<Void, ExampleLiveObjectA, ExampleFactoryA, Void> builder = new FactoryTreeBuilder<>(ExampleFactoryA.class);
        builder.addFactory(ExampleFactoryA.class, Scope.SINGLETON, ctx -> {
            ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
            exampleFactoryA.referenceAttribute.set(ctx.get(ExampleFactoryB.class));
            return exampleFactoryA;
        });
        builder.addFactory(ExampleFactoryB.class, Scope.SINGLETON, ctx-> {
            ExampleFactoryB exampleFactoryB=new ExampleFactoryB();
            exampleFactoryB.stringAttribute.set("111");
            return exampleFactoryB;
        });

        FactoryTreeBuilderAttributeFiller<Void, ExampleLiveObjectA, ExampleFactoryA, Void> filler = new FactoryTreeBuilderAttributeFiller<>(builder);

        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
        exampleFactoryA.internal().addBackReferences();
        DataStorageMetadataDictionary dataStorageMetadataDictionaryFromRoot = exampleFactoryA.internal().createDataStorageMetadataDictionaryFromRoot();


        ExampleFactoryA exampleFactoryNew = new ExampleFactoryA();
        exampleFactoryNew.internal().addBackReferences();
        Assertions.assertNull(exampleFactoryNew.referenceAttribute.get());
        filler.fillNewAttributes(exampleFactoryNew,dataStorageMetadataDictionaryFromRoot);
        Assertions.assertNull(exampleFactoryNew.referenceAttribute.get());
    }

    @Test
    public void test_add_singleton(){

        FactoryTreeBuilder<Void, ExampleLiveObjectA, ExampleFactoryA, Void> builder = new FactoryTreeBuilder<>(ExampleFactoryA.class);
        builder.addFactory(ExampleFactoryA.class, Scope.SINGLETON, ctx -> {
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

        FactoryTreeBuilderAttributeFiller<Void, ExampleLiveObjectA, ExampleFactoryA, Void> filler = new FactoryTreeBuilderAttributeFiller<>(builder);

        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
        exampleFactoryA.internal().addBackReferences();
        DataStorageMetadataDictionary dataStorageMetadataDictionaryFromRoot = exampleFactoryA.internal().createDataStorageMetadataDictionaryFromRoot();
        dataStorageMetadataDictionaryFromRoot.getDataStorageMetadata(ExampleFactoryA.class.getName()).removeAttribute("referenceAttribute");

        ExampleFactoryA exampleFactoryNew = new ExampleFactoryA();
        exampleFactoryNew.referenceAttribute.set(null);
        exampleFactoryNew.referenceListAttribute.add(new ExampleFactoryB());
        exampleFactoryNew.internal().addBackReferences();
        filler.fillNewAttributes(exampleFactoryNew,dataStorageMetadataDictionaryFromRoot);
        Assertions.assertEquals(exampleFactoryNew.referenceAttribute.get(),exampleFactoryNew.referenceListAttribute.get(0));
    }





}
