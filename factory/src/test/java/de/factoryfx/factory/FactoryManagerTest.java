package de.factoryfx.factory;

import java.util.ArrayList;
import java.util.function.Consumer;

import de.factoryfx.factory.exception.RethrowingFactoryExceptionHandler;
import de.factoryfx.factory.testfactories.*;
import org.junit.Assert;
import org.junit.Test;

public class FactoryManagerTest {

    @Test
    public void test(){
        FactoryManager<Void,ExampleFactoryA> factoryManager = new FactoryManager<>(new RethrowingFactoryExceptionHandler());

        ArrayList<String> calls =new ArrayList<>();

        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
        exampleFactoryA.configLiveCycle().setStarter(exampleLiveObjectA -> calls.add("start exampleFactoryA"));
        ExampleFactoryB exampleFactoryB = new ExampleFactoryB();
        exampleFactoryB.configLiveCycle().setStarter(exampleLiveObjectA -> calls.add("start exampleFactoryB"));



        exampleFactoryA.referenceAttribute.set(exampleFactoryB);
        factoryManager.start(new RootFactoryWrapper<>(exampleFactoryA));

        Assert.assertEquals(2,calls.size());
        Assert.assertEquals("start exampleFactoryB",calls.get(0));
        Assert.assertEquals("start exampleFactoryA",calls.get(1));
    }

    @Test
    public void test_remove_destroy_called(){
        FactoryManager<Void,ExampleFactoryA> factoryManager = new FactoryManager<>(new RethrowingFactoryExceptionHandler());

        ArrayList<String> calls =new ArrayList<>();

        ExampleFactoryA root = new ExampleFactoryA();
        ExampleFactoryB exampleFactoryB = new ExampleFactoryB();
        root.referenceAttribute.set(exampleFactoryB);
        root = root.internal().addBackReferences();
        root.configLiveCycle().setDestroyer(new Consumer<ExampleLiveObjectA>() {
            @Override
            public void accept(ExampleLiveObjectA exampleLiveObjectA) {
                calls.add("destroy");
            }
        });
        factoryManager.start(new RootFactoryWrapper<>(root));

        ExampleFactoryA update = root.internal().copy();
        update.referenceAttribute.set(null);


        factoryManager.update(root.utility().copy(),update,(p)->true);

        Assert.assertEquals(1,calls.size());
        Assert.assertEquals("destroy",calls.get(0));
    }

    @Test
    public void test_updated_destroy_called(){
        FactoryManager<Void,ExampleFactoryA> factoryManager = new FactoryManager<>(new RethrowingFactoryExceptionHandler());

        ArrayList<String> calls =new ArrayList<>();

        ExampleFactoryA root = new ExampleFactoryA();
        ExampleFactoryB exampleFactoryB = new ExampleFactoryB();
        root.referenceAttribute.set(exampleFactoryB);
        root = root.internal().addBackReferences();
        root.configLiveCycle().setDestroyer(exampleLiveObjectA -> calls.add("destroy for update"));
        factoryManager.start(new RootFactoryWrapper<>(root));

        ExampleFactoryA update = root.internal().copy();
        update.referenceAttribute.set(new ExampleFactoryB());


        factoryManager.update(root.utility().copy(),update,(p)->true);

        Assert.assertEquals(1,calls.size());
        Assert.assertEquals("destroy for update",calls.get(0));
    }

    @Test
    public void test_large_tree(){


        FactoryManager<Void,FastExampleFactoryA> factoryManager = new FactoryManager<>(new RethrowingFactoryExceptionHandler());


        FastExampleFactoryA exampleFactoryA = new FastExampleFactoryA();
        for (int i=0;i<100000;i++){
            FastExampleFactoryB factoryB = new FastExampleFactoryB();
            factoryB.referenceAttributeC=new FastExampleFactoryC();
            exampleFactoryA.referenceListAttribute.add(factoryB);
        }


//        FactoryManager<Void,ExampleFactoryA> factoryManager = new FactoryManager<>(new RethrowingFactoryExceptionHandler());
//        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
//        for (int i=0;i<100000;i++){
//            ExampleFactoryB factoryBases = new ExampleFactoryB();
//            factoryBases.referenceAttributeC.set(new ExampleFactoryC());
//            exampleFactoryA.referenceListAttribute.add(factoryBases);
//        }



        factoryManager.start(new RootFactoryWrapper<>(exampleFactoryA));

//        try {
//            Thread.sleep(1000000000);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//        factoryManager.getCurrentFactory();

    }


}