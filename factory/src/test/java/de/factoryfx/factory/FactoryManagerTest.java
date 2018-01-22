package de.factoryfx.factory;

import java.util.ArrayList;
import java.util.function.Consumer;

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

    @Test
    public void test_remove_destroy_called(){
        FactoryManager<Void,ExampleLiveObjectA,ExampleFactoryA> factoryManager = new FactoryManager<>(new RethrowingFactoryExceptionHandler<Void>());

        ArrayList<String> calls =new ArrayList<>();

        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
        ExampleFactoryB exampleFactoryB = new ExampleFactoryB();
        exampleFactoryA.referenceAttribute.set(exampleFactoryB);
        exampleFactoryA = exampleFactoryA.internal().prepareUsableCopy();
        exampleFactoryA.configLiveCycle().setDestroyer(new Consumer<ExampleLiveObjectA>() {
            @Override
            public void accept(ExampleLiveObjectA exampleLiveObjectA) {
                calls.add("destroy");
            }
        });
        factoryManager.start(exampleFactoryA);

        ExampleFactoryA update = exampleFactoryA.internal().copy();
        update.referenceAttribute.set(null);


        factoryManager.update(exampleFactoryA,update,(p)->true);

        Assert.assertEquals(1,calls.size());
        Assert.assertEquals("destroy",calls.get(0));
    }

    @Test
    public void test_updated_destroy_called(){
        FactoryManager<Void,ExampleLiveObjectA,ExampleFactoryA> factoryManager = new FactoryManager<>(new RethrowingFactoryExceptionHandler<Void>());

        ArrayList<String> calls =new ArrayList<>();

        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
        ExampleFactoryB exampleFactoryB = new ExampleFactoryB();
        exampleFactoryA.referenceAttribute.set(exampleFactoryB);
        exampleFactoryA = exampleFactoryA.internal().prepareUsableCopy();
        exampleFactoryA.configLiveCycle().setDestroyer(new Consumer<ExampleLiveObjectA>() {
            @Override
            public void accept(ExampleLiveObjectA exampleLiveObjectA) {
                calls.add("destroy for update");
            }
        });
        factoryManager.start(exampleFactoryA);

        ExampleFactoryA update = exampleFactoryA.internal().copy();
        update.referenceAttribute.set(new ExampleFactoryB());


        factoryManager.update(exampleFactoryA,update,(p)->true);

        Assert.assertEquals(1,calls.size());
        Assert.assertEquals("destroy for update",calls.get(0));
    }


}