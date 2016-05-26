package de.factoryfx.factory;

import java.util.LinkedHashMap;

import de.factoryfx.factory.testfactories.ExampleFactoryA;
import de.factoryfx.factory.testfactories.ExampleFactoryB;
import org.junit.Assert;
import org.junit.Test;

public class FactoryManagerTest {

    @Test
    public void test(){
        FactoryManager<ExampleFactoryA> factoryManager = new FactoryManager<>();

        ExampleFactoryA newFactory = new ExampleFactoryA();
        newFactory.referenceAttribute.set(new ExampleFactoryB());

        factoryManager.start(newFactory);

        LinkedHashMap<String, LiveObject> liveObjects = new LinkedHashMap<>();
        newFactory.collectLiveObjects(liveObjects);
        Assert.assertEquals(2,liveObjects.size());

    }

}