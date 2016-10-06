package de.factoryfx.factory;

import de.factoryfx.factory.testfactories.ExampleFactoryA;
import de.factoryfx.factory.testfactories.ExampleFactoryB;
import de.factoryfx.factory.testfactories.ExampleFactoryC;
import de.factoryfx.factory.testfactories.ExampleLiveObjectA;
import de.factoryfx.factory.testfactories.ExampleLiveObjectB;
import de.factoryfx.factory.testfactories.ExampleLiveObjectC;
import org.junit.Assert;
import org.junit.Test;

public class FactoryManagerTest {

    @Test
    public void test(){
        FactoryManager<ExampleLiveObjectA,Void,ExampleFactoryA> factoryManager = new FactoryManager<>();

        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
        ExampleFactoryB exampleFactoryB = new ExampleFactoryB();
        ExampleFactoryC exampleFactoryC = new ExampleFactoryC();
        exampleFactoryB.referenceAttributeC.set(exampleFactoryC);
        exampleFactoryA.referenceAttribute.set(exampleFactoryB);

        factoryManager.start(exampleFactoryA);

        exampleFactoryA.getCreatedLiveObject().isPresent();
        exampleFactoryB.getCreatedLiveObject().isPresent();
        exampleFactoryC.getCreatedLiveObject().isPresent();
    }


    @Test
    public void test_reuse_live_objects_part_differnt(){
        FactoryManager<ExampleLiveObjectA,Void,ExampleFactoryA> factoryManager = new FactoryManager<>();

        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();

        ExampleFactoryB exampleFactoryB = new ExampleFactoryB();
        ExampleFactoryC exampleFactoryC = new ExampleFactoryC();
        exampleFactoryB.referenceAttributeC.set(exampleFactoryC);
        exampleFactoryA.referenceAttribute.set(exampleFactoryB);
        exampleFactoryB.stringAttribute.set("first");


        factoryManager.start(exampleFactoryA);

        ExampleLiveObjectA exampleLiveObjectA = exampleFactoryA.getCreatedLiveObject().get();
        ExampleLiveObjectB exampleLiveObjectB = exampleFactoryB.getCreatedLiveObject().get();
        ExampleLiveObjectC exampleLiveObjectC = exampleFactoryC.getCreatedLiveObject().get();

        ExampleFactoryA change = exampleFactoryA.copy();
        change.referenceAttribute.get().stringAttribute.set("update");

        factoryManager.update(exampleFactoryA,change);

        Assert.assertNotEquals(exampleLiveObjectA,exampleFactoryA.getCreatedLiveObject().get());// root always change
        Assert.assertNotEquals(exampleLiveObjectB,exampleFactoryB.getCreatedLiveObject().get());
        Assert.assertEquals(exampleLiveObjectC,exampleFactoryC.getCreatedLiveObject().get());
    }

    @Test
    public void test_reuse_live_objects_all_same(){
        FactoryManager<ExampleLiveObjectA,Void,ExampleFactoryA> factoryManager = new FactoryManager<>();

        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();

        ExampleFactoryB exampleFactoryB = new ExampleFactoryB();
        ExampleFactoryC exampleFactoryC = new ExampleFactoryC();
        exampleFactoryB.referenceAttributeC.set(exampleFactoryC);
        exampleFactoryA.referenceAttribute.set(exampleFactoryB);
        exampleFactoryB.stringAttribute.set("first");


        factoryManager.start(exampleFactoryA);

        ExampleLiveObjectA exampleLiveObjectA = exampleFactoryA.getCreatedLiveObject().get();
        ExampleLiveObjectB exampleLiveObjectB = exampleFactoryB.getCreatedLiveObject().get();
        ExampleLiveObjectC exampleLiveObjectC = exampleFactoryC.getCreatedLiveObject().get();

        ExampleFactoryA change = exampleFactoryA.copy();

        factoryManager.update(exampleFactoryA,change);

        Assert.assertEquals(exampleLiveObjectA,exampleFactoryA.getCreatedLiveObject().get());// root always change
        Assert.assertEquals(exampleLiveObjectB,exampleFactoryB.getCreatedLiveObject().get());
        Assert.assertEquals(exampleLiveObjectC,exampleFactoryC.getCreatedLiveObject().get());
    }

    @Test
    public void test_reuse_live_objects_all_different(){
        FactoryManager<ExampleLiveObjectA,Void,ExampleFactoryA> factoryManager = new FactoryManager<>();

        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();

        ExampleFactoryB exampleFactoryB = new ExampleFactoryB();
        ExampleFactoryC exampleFactoryC = new ExampleFactoryC();
        exampleFactoryB.referenceAttributeC.set(exampleFactoryC);
        exampleFactoryA.referenceAttribute.set(exampleFactoryB);

        exampleFactoryA.stringAttribute.set("first1");
        exampleFactoryA.referenceAttribute.get().stringAttribute.set("first1");
        exampleFactoryA.referenceAttribute.get().referenceAttributeC.get().stringAttribute.set("first1");


        factoryManager.start(exampleFactoryA);

        ExampleLiveObjectA exampleLiveObjectA = exampleFactoryA.getCreatedLiveObject().get();
        ExampleLiveObjectB exampleLiveObjectB = exampleFactoryB.getCreatedLiveObject().get();
        ExampleLiveObjectC exampleLiveObjectC = exampleFactoryC.getCreatedLiveObject().get();

        ExampleFactoryA change = exampleFactoryA.copy();
        change.stringAttribute.set("update1");
        change.referenceAttribute.get().stringAttribute.set("update2");
        change.referenceAttribute.get().referenceAttributeC.get().stringAttribute.set("update3");

        factoryManager.update(exampleFactoryA,change);


        Assert.assertNotEquals(exampleLiveObjectA,exampleFactoryA.getCreatedLiveObject().get());// root always change
        Assert.assertNotEquals(exampleLiveObjectB,exampleFactoryB.getCreatedLiveObject().get());
        Assert.assertNotEquals(exampleLiveObjectC,exampleFactoryC.getCreatedLiveObject().get());
    }

}