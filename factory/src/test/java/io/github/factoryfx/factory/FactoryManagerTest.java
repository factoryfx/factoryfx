package io.github.factoryfx.factory;

import java.util.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;
import io.github.factoryfx.factory.attribute.dependency.FactoryListAttribute;
import io.github.factoryfx.factory.attribute.dependency.FactoryViewAttribute;
import io.github.factoryfx.factory.attribute.types.StringAttribute;
import io.github.factoryfx.factory.exception.ResettingHandler;
import io.github.factoryfx.factory.exception.RethrowingFactoryExceptionHandler;
import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;
import io.github.factoryfx.factory.log.FactoryUpdateLog;
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

    @Test
    public void test_update() {
        FactoryManager<ExampleLiveObjectA, ExampleFactoryA> factoryManager = new FactoryManager<ExampleLiveObjectA, ExampleFactoryA>(new RethrowingFactoryExceptionHandler<>());
        ExampleFactoryA root1 = new ExampleFactoryA();
        factoryManager.start(new RootFactoryWrapper<>(root1));

        ExampleFactoryB addedfactory = new ExampleFactoryB();
        FactoryUpdateLog<ExampleFactoryA> log = factoryManager.update((root, idToFactory) -> {
            root.referenceAttribute.set(addedfactory);
        });

        Assertions.assertEquals(addedfactory,factoryManager.getCurrentFactory().referenceAttribute.get());
        Assertions.assertNotNull(factoryManager.getCurrentFactory().referenceAttribute.get().internal().getLiveObject());
    }

    @Test
    public void test_update_twice() {
        FactoryManager<ExampleLiveObjectA, ExampleFactoryA> factoryManager = new FactoryManager<ExampleLiveObjectA, ExampleFactoryA>(new RethrowingFactoryExceptionHandler<>());
        ExampleFactoryA root1 = new ExampleFactoryA();
        factoryManager.start(new RootFactoryWrapper<>(root1));

        factoryManager.update((root, idToFactory) -> {
            root.referenceAttribute.set(new ExampleFactoryB());
        });

        ExampleFactoryB addedfactory = new ExampleFactoryB();
        factoryManager.update((root, idToFactory) -> {
            root.referenceAttribute.set(addedfactory);
        });

        Assertions.assertEquals(addedfactory,factoryManager.getCurrentFactory().referenceAttribute.get());
    }

    @Test
    public void test_update_exception() {
        FactoryManager<ExampleLiveObjectA, ExampleFactoryA> factoryManager = new FactoryManager<ExampleLiveObjectA, ExampleFactoryA>(new ResettingHandler<>());
        ExampleFactoryA root1 = new ExampleFactoryA();
        factoryManager.start(new RootFactoryWrapper<>(root1));

        ExampleFactoryB addedfactory = new ExampleFactoryB();
        factoryManager.update((root, idToFactory) -> {
            root.referenceAttribute.set(addedfactory);
            throw new RuntimeException();
        });

        Assertions.assertEquals(null,factoryManager.getCurrentFactory().referenceAttribute.get());
    }

    public static class ExampleFactoryViewRoot extends SimpleFactoryBase<Void,ExampleFactoryViewRoot> {

        public final FactoryListAttribute<Void, ExampleFactoryWithViewAttribute> refView = new FactoryListAttribute<>();
        public final FactoryAttribute<Void,ExampleFactoryViewA> refA = new FactoryAttribute<>();

        @Override
        protected Void createImpl() {
            return null;
        }
    }

    public static class ExampleFactoryViewA extends SimpleFactoryBase<Void,ExampleFactoryViewRoot> {
        @JsonIgnore
        public int createCounter=0;

        @Override
        protected Void createImpl() {
            createCounter++;
            return null;
        }
    }


    public static class ExampleFactoryWithViewAttribute extends SimpleFactoryBase<Void,ExampleFactoryViewRoot> {
        public final FactoryViewAttribute<ExampleFactoryViewRoot,Void,ExampleFactoryViewA> referenceView = new FactoryViewAttribute<>(
                root -> root.refA.get());

        public StringAttribute stringAttribute = new StringAttribute();

        @Override
        protected Void createImpl() {
            return null;
        }

        @Override
        public String toString() {
            return getClass().getSimpleName()+":"+stringAttribute.toString();
        }
    }

    @Test
    public void test_add_view() {
        FactoryManager<Void, ExampleFactoryViewRoot> factoryManager = new FactoryManager<Void, ExampleFactoryViewRoot>(new RethrowingFactoryExceptionHandler<>());

        ExampleFactoryViewRoot root = new ExampleFactoryViewRoot();
        root.refA.set(new ExampleFactoryViewA());
        ExampleFactoryWithViewAttribute value = new ExampleFactoryWithViewAttribute();
        value.stringAttribute.set("initial");
        root.refView.add(value);
        factoryManager.start(new RootFactoryWrapper<>(root));

        {
            ExampleFactoryViewRoot update2 = ObjectMapperBuilder.build().copy(root);
            ExampleFactoryWithViewAttribute value1 = new ExampleFactoryWithViewAttribute();
            value1.stringAttribute.set("added");
            update2.refView.add(value1);
            update2.refA.get().createCounter=0;
            factoryManager.update(root.utility().copy(), update2, (p) -> true);
            Assertions.assertEquals(0, update2.refA.get().createCounter);
        }

    }

}