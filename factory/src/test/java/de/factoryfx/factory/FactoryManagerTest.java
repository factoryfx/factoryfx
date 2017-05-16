package de.factoryfx.factory;

import java.util.ArrayList;

import de.factoryfx.factory.exception.RethrowingFactoryExceptionHandler;
import de.factoryfx.factory.testfactories.ExampleFactoryA;
import de.factoryfx.factory.testfactories.ExampleFactoryB;
import de.factoryfx.factory.testfactories.ExampleLiveObjectA;
import org.junit.Assert;
import org.junit.Test;

public class FactoryManagerTest {

    @Test
    public void test(){
        FactoryManager<Void,ExampleLiveObjectA,ExampleFactoryA> factoryManager = new FactoryManager<>(new RethrowingFactoryExceptionHandler<Void>());

        ArrayList<String> calls =new ArrayList<>();

        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
        exampleFactoryA.configLiveCycle().setStarter(exampleLiveObjectA -> calls.add("start exampleFactoryA"));
        ExampleFactoryB exampleFactoryB = new ExampleFactoryB();
        exampleFactoryB.configLiveCycle().setStarter(exampleLiveObjectA -> calls.add("start exampleFactoryB"));



        exampleFactoryA.referenceAttribute.set(exampleFactoryB);
        factoryManager.start(exampleFactoryA);

        Assert.assertEquals(2,calls.size());
        Assert.assertEquals("start exampleFactoryB",calls.get(0));
        Assert.assertEquals("start exampleFactoryA",calls.get(1));
    }

}