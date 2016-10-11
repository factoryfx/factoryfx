package de.factoryfx.factory;

import de.factoryfx.factory.testfactories.ExampleFactoryA;
import de.factoryfx.factory.testfactories.ExampleFactoryB;
import de.factoryfx.factory.testfactories.ExampleLiveObjectA;
import org.junit.Test;

public class FactoryManagerTest {

    @Test
    public void test(){
        FactoryManager<ExampleLiveObjectA,Void,ExampleFactoryA> factoryManager = new FactoryManager<>();

        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
        ExampleFactoryB exampleFactoryB = new ExampleFactoryB();
        exampleFactoryA.referenceAttribute.set(exampleFactoryB);

        factoryManager.start(exampleFactoryA);

        exampleFactoryA.getCreatedLiveObject().isPresent();
        exampleFactoryB.getCreatedLiveObject().isPresent();
    }

}