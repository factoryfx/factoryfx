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
        FactoryManager<ExampleFactoryA> factoryManager = new FactoryManager<>();

        ExampleFactoryA newFactory = new ExampleFactoryA();
        ExampleFactoryB exampleFactoryB = new ExampleFactoryB();
        exampleFactoryB.referenceAttributeC.set(new ExampleFactoryC());
        newFactory.referenceAttribute.set(exampleFactoryB);

        factoryManager.start(newFactory);

        LinkedHashMap<String, LiveObject> liveObjects = new LinkedHashMap<>();
        newFactory.collectLiveObjects(liveObjects);
        Assert.assertEquals(3,liveObjects.size());

    }

}