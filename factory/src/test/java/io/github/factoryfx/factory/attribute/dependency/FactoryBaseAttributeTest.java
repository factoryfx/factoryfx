package io.github.factoryfx.factory.attribute.dependency;

import io.github.factoryfx.factory.FactoryTreeBuilderBasedAttributeSetup;
import io.github.factoryfx.factory.attribute.AttributeChangeListener;
import io.github.factoryfx.factory.builder.FactoryTemplateId;
import io.github.factoryfx.factory.builder.FactoryTreeBuilder;
import io.github.factoryfx.factory.builder.Scope;
import io.github.factoryfx.factory.testfactories.ExampleFactoryA;
import io.github.factoryfx.factory.testfactories.ExampleFactoryB;
import io.github.factoryfx.factory.testfactories.ExampleFactoryC;
import io.github.factoryfx.factory.testfactories.ExampleLiveObjectA;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FactoryBaseAttributeTest {

    @Test
    public void test_Listener()  {
        List<ExampleFactoryA> calls= new ArrayList<>();

        FactoryAttribute<ExampleLiveObjectA, ExampleFactoryA> factoryAttribute = new FactoryAttribute<>();
        final AttributeChangeListener<ExampleFactoryA,FactoryAttribute<ExampleLiveObjectA, ExampleFactoryA>> stringAttributeChangeListener = (attribute, value) -> calls.add(value);
        factoryAttribute.internal_addListener(stringAttributeChangeListener);

        ExampleFactoryA value = new ExampleFactoryA();
        factoryAttribute.set(value);

        Assertions.assertEquals(1,calls.size());
        Assertions.assertEquals(value,calls.get(0));
    }

    @Test
    public void test_back_references_after_set()  {
        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
        exampleFactoryA.internal().finalise();
        exampleFactoryA.referenceAttribute.set(new ExampleFactoryB());

        exampleFactoryA.internal().finalise();
        assertEquals(1,exampleFactoryA.referenceAttribute.get().internal().getParents().size());
    }

    @Test
    public void test_back_references_after_set_double()  {
        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();


        ExampleFactoryB factory = new ExampleFactoryB();
        ExampleFactoryC exampleFactoryC = new ExampleFactoryC();
        factory.referenceAttributeC.set(exampleFactoryC);
        exampleFactoryA.referenceAttribute.set(factory);

        exampleFactoryA.internal().finalise();

        ExampleFactoryB factoryB = new ExampleFactoryB();
        exampleFactoryC.referenceAttribute.set(factoryB);
        exampleFactoryA.referenceListAttribute.add(factoryB);

        exampleFactoryA.internal().finalise();
        assertEquals(2,factoryB.internal().getParents().size());


    }


    @Test
    public void test_createNew()  {
        FactoryTreeBuilder<ExampleLiveObjectA,ExampleFactoryA> builder = new FactoryTreeBuilder<>(ExampleFactoryA.class, ctx -> {
            ExampleFactoryA root = new ExampleFactoryA();
            root.referenceAttribute.set(ctx.get(ExampleFactoryB.class,"test"));
            return root;
        });
        builder.addFactory(ExampleFactoryB.class,"test", Scope.SINGLETON, ctx -> {
            ExampleFactoryB exampleFactoryB = new ExampleFactoryB();
            exampleFactoryB.stringAttribute.set("bla");
            return exampleFactoryB;
        });
        ExampleFactoryA root = builder.buildTreeUnvalidated();
        root.referenceAttribute.set(null);
        root.internal().serFactoryTreeBuilderBasedAttributeSetupForRoot(new FactoryTreeBuilderBasedAttributeSetup<>(builder));

        ExampleFactoryB possibleValue = root.referenceAttribute.internal_createNewPossibleValues(root.internal().getAttributeMetadata(root.referenceAttribute)).get(0).newValue;
        Assertions.assertEquals("bla",possibleValue.stringAttribute.get());

        Assertions.assertEquals(ExampleFactoryB.class,new FactoryTemplateId<>(possibleValue).clazz);
        Assertions.assertEquals("test",new FactoryTemplateId<>(possibleValue).name);
    }

    @Test
    public void test_reset()  {
        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
        ExampleFactoryB factory = new ExampleFactoryB();
        exampleFactoryA.referenceAttribute.set(factory);
        exampleFactoryA.internal().finalise();

        exampleFactoryA.referenceAttribute.set(new ExampleFactoryB());
        exampleFactoryA.internal().resetModificationFlat();
        assertEquals(factory,exampleFactoryA.referenceAttribute.get());
    }

    @Test
    public void test_reset_setMultiple()  {
        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
        ExampleFactoryB factory = new ExampleFactoryB();
        exampleFactoryA.referenceAttribute.set(factory);
        exampleFactoryA.internal().finalise();

        exampleFactoryA.referenceAttribute.set(new ExampleFactoryB());
        exampleFactoryA.referenceAttribute.set(new ExampleFactoryB());
        exampleFactoryA.referenceAttribute.set(new ExampleFactoryB());

        exampleFactoryA.internal().resetModificationFlat();
        assertEquals(factory,exampleFactoryA.referenceAttribute.get());
    }

    @Test
    public void test_reset_null()  {
        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
        exampleFactoryA.internal().finalise();

        exampleFactoryA.referenceAttribute.set(new ExampleFactoryB());
        exampleFactoryA.internal().resetModificationFlat();
        assertEquals(null,exampleFactoryA.referenceAttribute.get());
    }

    @Test
    public void test_reset_setNull()  {
        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
        ExampleFactoryB factory = new ExampleFactoryB();
        exampleFactoryA.referenceAttribute.set(factory);
        exampleFactoryA.internal().finalise();

        exampleFactoryA.referenceAttribute.set(null);
        exampleFactoryA.internal().resetModificationFlat();
        assertEquals(factory,exampleFactoryA.referenceAttribute.get());
    }
}