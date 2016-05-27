package de.factoryfx.factory;

import java.util.ArrayList;
import java.util.HashMap;

import de.factoryfx.factory.testfactories.ExampleFactoryA;
import de.factoryfx.factory.testfactories.ExampleFactoryB;
import de.factoryfx.factory.testfactories.ExampleFactoryC;
import org.junit.Assert;
import org.junit.Test;

public class FactoryBaseTest {

    @Test(expected = IllegalStateException.class)
    public void create_loop_test(){
        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
        ExampleFactoryB exampleFactoryB = new ExampleFactoryB();
        exampleFactoryB.referenceAttribute.set(exampleFactoryA);
        exampleFactoryA.referenceAttribute.set(exampleFactoryB);

        exampleFactoryA.loopDetector();
    }

    @Test
    public void test_collect_Live_Objects(){
        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
        ExampleFactoryB exampleFactoryB = new ExampleFactoryB();
        ExampleFactoryC exampleFactoryC = new ExampleFactoryC();
        exampleFactoryB.referenceAttributeC.set(exampleFactoryC);

        exampleFactoryA.referenceAttribute.set(exampleFactoryB);

        exampleFactoryA.create();

        HashMap<String, LiveObject> liveObjects = new HashMap<>();
        exampleFactoryA.collectLiveObjects(liveObjects);

        Assert.assertEquals(3,liveObjects.entrySet().size());
    }


    @Test
    public void test_visitAttributes(){
        ExampleFactoryA testModel = new ExampleFactoryA();
        testModel.stringAttribute.set("xxxx");
        testModel.referenceAttribute.set(new ExampleFactoryB());

        ArrayList<String> calls = new ArrayList<>();
        testModel.visitAttributesFlat(attribute -> calls.add(attribute.get().toString()));
        Assert.assertEquals(3,calls.size());
        Assert.assertEquals("xxxx",calls.get(0));
    }


}