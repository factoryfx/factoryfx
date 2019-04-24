package io.github.factoryfx.factory;

import java.util.ArrayList;
import java.util.Set;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import io.github.factoryfx.factory.exception.RethrowingFactoryExceptionHandler;
import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;
import io.github.factoryfx.factory.testfactories.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;


public class FactoryManagerTest {

    @Test
    public void test(){
        FactoryManager<ExampleLiveObjectA, ExampleFactoryA> factoryManager = new FactoryManager<>(new RethrowingFactoryExceptionHandler());

        ArrayList<String> calls =new ArrayList<>();

        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
        exampleFactoryA.configLifeCycle().setStarter(exampleLiveObjectA -> calls.add("start exampleFactoryA"));
        ExampleFactoryB exampleFactoryB = new ExampleFactoryB();
        exampleFactoryB.configLifeCycle().setStarter(exampleLiveObjectA -> calls.add("start exampleFactoryB"));



        exampleFactoryA.referenceAttribute.set(exampleFactoryB);
        factoryManager.start(new RootFactoryWrapper<>(exampleFactoryA));

        Assertions.assertEquals(2,calls.size());
        Assertions.assertEquals("start exampleFactoryB",calls.get(0));
        Assertions.assertEquals("start exampleFactoryA",calls.get(1));
    }

    @Test
    public void test_remove_destroy_called(){
        FactoryManager<ExampleLiveObjectA,ExampleFactoryA> factoryManager = new FactoryManager<>(new RethrowingFactoryExceptionHandler());

        ArrayList<String> calls =new ArrayList<>();

        ExampleFactoryA root = new ExampleFactoryA();
        ExampleFactoryB exampleFactoryB = new ExampleFactoryB();
        root.referenceAttribute.set(exampleFactoryB);
        root = root.internal().addBackReferences();
        root.configLifeCycle().setDestroyer(exampleLiveObjectA -> calls.add("destroy"));
        factoryManager.start(new RootFactoryWrapper<>(root));

        ExampleFactoryA update = root.internal().copy();
        update.referenceAttribute.set(null);


        factoryManager.update(root.utility().copy(),update,(p)->true);

        Assertions.assertEquals(1,calls.size());
        Assertions.assertEquals("destroy",calls.get(0));
    }

    @Test
    public void test_updated_destroy_called(){
        FactoryManager<ExampleLiveObjectA,ExampleFactoryA> factoryManager = new FactoryManager<>(new RethrowingFactoryExceptionHandler());

        ArrayList<String> calls =new ArrayList<>();

        ExampleFactoryA root = new ExampleFactoryA();
        ExampleFactoryB exampleFactoryB = new ExampleFactoryB();
        root.referenceAttribute.set(exampleFactoryB);
        root = root.internal().addBackReferences();
        root.configLifeCycle().setDestroyer(exampleLiveObjectA -> calls.add("destroy for update"));
        factoryManager.start(new RootFactoryWrapper<>(root));

        ExampleFactoryA update = root.internal().copy();
        update.referenceAttribute.set(new ExampleFactoryB());


        factoryManager.update(root.utility().copy(),update,(p)->true);

        Assertions.assertEquals(1,calls.size());
        Assertions.assertEquals("destroy for update",calls.get(0));
    }

    @Test
    public void test_large_tree(){

        FactoryManager<ExampleLiveObjectA, FastExampleFactoryA> factoryManager = new FactoryManager<>(new RethrowingFactoryExceptionHandler());


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
//        factoryManager.getCurrentData();

    }


    @Test
    public void test_large_tree_normal_factories(){
        FactoryManager<ExampleLiveObjectA, ExampleFactoryA> factoryManager = new FactoryManager<>(new RethrowingFactoryExceptionHandler());

        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
        for (int i=0;i<100000;i++){
            ExampleFactoryB factoryBases = new ExampleFactoryB();
            factoryBases.referenceAttributeC.set(new ExampleFactoryC());
            exampleFactoryA.referenceListAttribute.add(factoryBases);
        }

        factoryManager.start(new RootFactoryWrapper<>(exampleFactoryA));

//        try {
//            Thread.sleep(1000000000);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//        factoryManager.getCurrentData();

    }

    public static void main(String[] args) {
        int count = 1;
//        for (int i = 0; i < count; i++) {
////            new FactoryManagerTest().test_large_tree_normal_factories();
////        }

        FactoryManager<ExampleLiveObjectA, FastExampleFactoryA> factoryManager = new FactoryManager<>(new RethrowingFactoryExceptionHandler());


        FastExampleFactoryA root = new FastExampleFactoryA();
        for (int i=0;i<100000;i++){
            FastExampleFactoryB factoryBases = new FastExampleFactoryB();
            factoryBases.referenceAttributeC=new FastExampleFactoryC();
            root.referenceListAttribute.add(factoryBases);
        }

        FastExampleFactoryA commonVersion = root.internal().copy();
        FastExampleFactoryA newVersion = root.internal().copy();

        factoryManager.start(new RootFactoryWrapper<>(root));
        long start = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            factoryManager.update(commonVersion, newVersion, (p) -> true);
        }
        System.out.println((System.currentTimeMillis()-start)/count);
    }

    @Test
    public void test_views_parent() {
        FactoryBaseTest.XRoot root = new FactoryBaseTest.XRoot();
        FactoryBaseTest.ExampleFactoryAndViewA exampleFactoryAndViewA = new FactoryBaseTest.ExampleFactoryAndViewA();
        root.referenceAttribute.set(exampleFactoryAndViewA);
        FactoryBaseTest.XFactory xFactory = new FactoryBaseTest.XFactory();
        root.xFactory.set(xFactory);

        root.internal().addBackReferences();

        xFactory.internal().getParents().contains(exampleFactoryAndViewA);
        xFactory.internal().getParents().contains(root);
    }

    @Test
    public void test_getChangedFactories_views() {
        FactoryBaseTest.XRoot root = new FactoryBaseTest.XRoot();
        FactoryBaseTest.ExampleFactoryAndViewA exampleFactoryAndViewA = new FactoryBaseTest.ExampleFactoryAndViewA();
        root.referenceAttribute.set(exampleFactoryAndViewA);
        FactoryBaseTest.XFactory xFactory = new FactoryBaseTest.XFactory();
        root.xFactory.set(xFactory);

        FactoryManager<String,FactoryBaseTest.XRoot> factoryManager = new FactoryManager<>(new RethrowingFactoryExceptionHandler());

        FactoryBaseTest.XRoot previous= root.utility().copy();
        root.xFactory.set(null);

        Set<FactoryBase<?,FactoryBaseTest.XRoot>> changedFactories = factoryManager.getChangedFactories(new RootFactoryWrapper<>(root), previous);
        Assertions.assertTrue(changedFactories.contains(exampleFactoryAndViewA));
    }

    @Test
    public void test_getChangedFactories_simple() {
        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
        exampleFactoryA.stringAttribute.set("123");

        ExampleFactoryA copy = exampleFactoryA.internal().copy();
        copy.stringAttribute.set("qqqqq");


        FactoryManager<ExampleLiveObjectA,ExampleFactoryA> factoryManager = new FactoryManager<>(new RethrowingFactoryExceptionHandler());
        Set<FactoryBase<?,ExampleFactoryA>> changedFactories = factoryManager.getChangedFactories(new RootFactoryWrapper<>(exampleFactoryA), copy);
        Assertions.assertEquals(1,changedFactories.size());
        Assertions.assertTrue(changedFactories.contains(exampleFactoryA));
    }

    @Test
    public void test_getChangedFactories_simple_change_to_null() {
        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
        exampleFactoryA.stringAttribute.set("123");

        ExampleFactoryA copy = exampleFactoryA.internal().copy();
        copy.stringAttribute.set(null);


        FactoryManager<ExampleLiveObjectA,ExampleFactoryA> factoryManager = new FactoryManager<>(new RethrowingFactoryExceptionHandler());
        Set<FactoryBase<?,ExampleFactoryA>> changedFactories = factoryManager.getChangedFactories(new RootFactoryWrapper<>(exampleFactoryA), copy);
        Assertions.assertEquals(1,changedFactories.size());
        Assertions.assertTrue(changedFactories.contains(exampleFactoryA));
    }

    @Test
    public void test_getChangedFactories_no_change() {
        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
        exampleFactoryA.stringAttribute.set("123");

        ExampleFactoryA copy = exampleFactoryA.internal().copy();
        copy.stringAttribute.set("123");


        FactoryManager<ExampleLiveObjectA,ExampleFactoryA> factoryManager = new FactoryManager<>(new RethrowingFactoryExceptionHandler());
        Set<FactoryBase<?,ExampleFactoryA>> changedFactories = factoryManager.getChangedFactories(new RootFactoryWrapper<>(exampleFactoryA), copy);
        Assertions.assertEquals(0,changedFactories.size());
    }

}