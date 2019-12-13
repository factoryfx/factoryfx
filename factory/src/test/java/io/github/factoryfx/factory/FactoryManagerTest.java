package io.github.factoryfx.factory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import io.github.factoryfx.factory.exception.RethrowingFactoryExceptionHandler;
import io.github.factoryfx.factory.testfactories.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class FactoryManagerTest {

    @Test
    public void test(){
        FactoryManager<ExampleLiveObjectA, ExampleFactoryA> factoryManager = new FactoryManager<ExampleLiveObjectA, ExampleFactoryA>(new RethrowingFactoryExceptionHandler<>());

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
        FactoryManager<ExampleLiveObjectA,ExampleFactoryA> factoryManager = new FactoryManager<ExampleLiveObjectA,ExampleFactoryA>(new RethrowingFactoryExceptionHandler<>());

        ArrayList<String> calls =new ArrayList<>();

        ExampleFactoryA root = new ExampleFactoryA();
        ExampleFactoryB exampleFactoryB = new ExampleFactoryB();
        root.referenceAttribute.set(exampleFactoryB);
        root = root.internal().finalise();
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
        FactoryManager<ExampleLiveObjectA,ExampleFactoryA> factoryManager = new FactoryManager<ExampleLiveObjectA,ExampleFactoryA>(new RethrowingFactoryExceptionHandler<>());

        ArrayList<String> calls =new ArrayList<>();

        ExampleFactoryA root = new ExampleFactoryA();
        ExampleFactoryB exampleFactoryB = new ExampleFactoryB();
        root.referenceAttribute.set(exampleFactoryB);
        root = root.internal().finalise();
        root.configLifeCycle().setDestroyer(exampleLiveObjectA -> calls.add("destroy for update"));
        factoryManager.start(new RootFactoryWrapper<>(root));

        ExampleFactoryA update = root.internal().copy();
        update.referenceAttribute.set(new ExampleFactoryB());


        factoryManager.update(root.utility().copy(),update,(p)->true);

        Assertions.assertEquals(1,calls.size());
        Assertions.assertEquals("destroy for update",calls.get(0));
    }


    @Test
    public void test_views_parent() {
        FactoryBaseTest.XRoot root = new FactoryBaseTest.XRoot();
        FactoryBaseTest.ExampleFactoryAndViewA exampleFactoryAndViewA = new FactoryBaseTest.ExampleFactoryAndViewA();
        root.referenceAttribute.set(exampleFactoryAndViewA);
        FactoryBaseTest.XFactory xFactory = new FactoryBaseTest.XFactory();
        root.xFactory.set(xFactory);

        root.internal().finalise();

        xFactory.internal().getParents().contains(exampleFactoryAndViewA);
        xFactory.internal().getParents().contains(root);
    }

    @Test
    public void test_getRemoved_factories() {
        FactoryManager<ExampleLiveObjectA, ExampleFactoryA> factoryManager = new FactoryManager<ExampleLiveObjectA, ExampleFactoryA>(new RethrowingFactoryExceptionHandler<>());


        ArrayList<FactoryBase<?, ExampleFactoryA>> previousFactories = new ArrayList<>();
        HashSet<FactoryBase<?, ExampleFactoryA>> newFactories = new HashSet<>();
        ExampleFactoryB both = new ExampleFactoryB();
        previousFactories.add(both);
        newFactories.add(both);
        ExampleFactoryB removed = new ExampleFactoryB();
        previousFactories.add(removed);

        List<FactoryBase<?, ExampleFactoryA>> removedFactories = factoryManager.getRemovedFactories(previousFactories, newFactories);

        Assertions.assertEquals(1,removedFactories.size());
        Assertions.assertEquals(removed,removedFactories.get(0));
    }

    @Test
    public void test_getRemoved_factories_no_removed() {
        FactoryManager<ExampleLiveObjectA, ExampleFactoryA> factoryManager = new FactoryManager<ExampleLiveObjectA, ExampleFactoryA>(new RethrowingFactoryExceptionHandler<>());


        ArrayList<FactoryBase<?, ExampleFactoryA>> previousFactories = new ArrayList<>();
        HashSet<FactoryBase<?, ExampleFactoryA>> newFactories = new HashSet<>();
        ExampleFactoryB both = new ExampleFactoryB();
        previousFactories.add(both);
        newFactories.add(both);

        List<FactoryBase<?, ExampleFactoryA>> removedFactories = factoryManager.getRemovedFactories(previousFactories, newFactories);
        Assertions.assertEquals(0,removedFactories.size());
    }

}