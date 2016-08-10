package de.factoryfx.factory;

import java.util.LinkedHashMap;

import de.factoryfx.factory.testfactories.ExampleFactoryA;
import de.factoryfx.factory.testfactories.ExampleFactoryB;
import de.factoryfx.factory.testfactories.ExampleFactoryC;
import org.junit.Assert;
import org.junit.Test;

public class FactoryManagerTest {

    @Test
    public void test(){
        FactoryManager<Void,ExampleFactoryA> factoryManager = new FactoryManager<>();

        ExampleFactoryA newFactory = new ExampleFactoryA();
        ExampleFactoryB exampleFactoryB = new ExampleFactoryB();
        exampleFactoryB.referenceAttributeC.set(new ExampleFactoryC());
        newFactory.referenceAttribute.set(exampleFactoryB);

        factoryManager.start(newFactory);

        LinkedHashMap<String, LiveObject> liveObjects = new LinkedHashMap<>();
        newFactory.collectLiveObjects(liveObjects);
        Assert.assertEquals(3,liveObjects.size());

    }


    @Test
    public void test_reuse_live_objects_part_differnt(){
        FactoryManager<Void,ExampleFactoryA> factoryManager = new FactoryManager<>();

        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();

        ExampleFactoryB exampleFactoryB = new ExampleFactoryB();
        ExampleFactoryC exampleFactoryC = new ExampleFactoryC();
        exampleFactoryB.referenceAttributeC.set(exampleFactoryC);
        exampleFactoryA.referenceAttribute.set(exampleFactoryB);
        exampleFactoryB.stringAttribute.set("first");


        factoryManager.start(exampleFactoryA);

        LinkedHashMap<String, LiveObject> liveObjects = new LinkedHashMap<>();
        exampleFactoryA.collectLiveObjects(liveObjects);
        Assert.assertEquals(3,liveObjects.size());



        ExampleFactoryA change = exampleFactoryA.copy();
        change.referenceAttribute.get().stringAttribute.set("update");

        factoryManager.update(exampleFactoryA,change);


        LinkedHashMap<String, LiveObject> liveObjectsAfterChange = new LinkedHashMap<>();
        exampleFactoryA.collectLiveObjects(liveObjectsAfterChange);
        Assert.assertEquals(3,liveObjectsAfterChange.size());

        Assert.assertNotEquals(liveObjects.get(exampleFactoryA.getId()),liveObjectsAfterChange.get(exampleFactoryA.getId()));// root always change
        Assert.assertNotEquals(liveObjects.get(exampleFactoryB.getId()),liveObjectsAfterChange.get(exampleFactoryB.getId()));
        Assert.assertEquals(liveObjects.get(exampleFactoryC.getId()),liveObjectsAfterChange.get(exampleFactoryC.getId()));
    }

    @Test
    public void test_reuse_live_objects_all_same(){
        FactoryManager<Void,ExampleFactoryA> factoryManager = new FactoryManager<>();

        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();

        ExampleFactoryB exampleFactoryB = new ExampleFactoryB();
        ExampleFactoryC exampleFactoryC = new ExampleFactoryC();
        exampleFactoryB.referenceAttributeC.set(exampleFactoryC);
        exampleFactoryA.referenceAttribute.set(exampleFactoryB);
        exampleFactoryB.stringAttribute.set("first");


        factoryManager.start(exampleFactoryA);

        LinkedHashMap<String, LiveObject> liveObjects = new LinkedHashMap<>();
        exampleFactoryA.collectLiveObjects(liveObjects);
        Assert.assertEquals(3,liveObjects.size());

        ExampleFactoryA change = exampleFactoryA.copy();
//        change.referenceAttribute.get().stringAttribute.set("update");

        factoryManager.update(exampleFactoryA,change);


        LinkedHashMap<String, LiveObject> liveObjectsAfterChange = new LinkedHashMap<>();
        exampleFactoryA.collectLiveObjects(liveObjectsAfterChange);
        Assert.assertEquals(3,liveObjectsAfterChange.size());

        Assert.assertEquals(liveObjects.get(exampleFactoryA.getId()),liveObjectsAfterChange.get(exampleFactoryA.getId()));
        Assert.assertEquals(liveObjects.get(exampleFactoryB.getId()),liveObjectsAfterChange.get(exampleFactoryB.getId()));
        Assert.assertEquals(liveObjects.get(exampleFactoryC.getId()),liveObjectsAfterChange.get(exampleFactoryC.getId()));
    }

    @Test
    public void test_reuse_live_objects_all_different(){
        FactoryManager<Void,ExampleFactoryA> factoryManager = new FactoryManager<>();

        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();

        ExampleFactoryB exampleFactoryB = new ExampleFactoryB();
        ExampleFactoryC exampleFactoryC = new ExampleFactoryC();
        exampleFactoryB.referenceAttributeC.set(exampleFactoryC);
        exampleFactoryA.referenceAttribute.set(exampleFactoryB);

        exampleFactoryA.stringAttribute.set("first1");
        exampleFactoryA.referenceAttribute.get().stringAttribute.set("first1");
        exampleFactoryA.referenceAttribute.get().referenceAttributeC.get().stringAttribute.set("first1");


        factoryManager.start(exampleFactoryA);

        LinkedHashMap<String, LiveObject> liveObjects = new LinkedHashMap<>();
        exampleFactoryA.collectLiveObjects(liveObjects);
        Assert.assertEquals(3,liveObjects.size());

        ExampleFactoryA change = exampleFactoryA.copy();
        change.stringAttribute.set("update1");
        change.referenceAttribute.get().stringAttribute.set("update2");
        change.referenceAttribute.get().referenceAttributeC.get().stringAttribute.set("update3");

        factoryManager.update(exampleFactoryA,change);


        LinkedHashMap<String, LiveObject> liveObjectsAfterChange = new LinkedHashMap<>();
        exampleFactoryA.collectLiveObjects(liveObjectsAfterChange);
        Assert.assertEquals(3,liveObjectsAfterChange.size());

        Assert.assertNotEquals(liveObjects.get(exampleFactoryA.getId()),liveObjectsAfterChange.get(exampleFactoryA.getId()));
        Assert.assertNotEquals(liveObjects.get(exampleFactoryB.getId()),liveObjectsAfterChange.get(exampleFactoryB.getId()));
        Assert.assertNotEquals(liveObjects.get(exampleFactoryC.getId()),liveObjectsAfterChange.get(exampleFactoryC.getId()));
    }

}