package de.factoryfx.factory;

import java.util.ArrayList;
import java.util.List;

import de.factoryfx.data.DataDictionary;
import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.factory.atrribute.FactoryReferenceListAttribute;
import de.factoryfx.factory.atrribute.FactoryViewListReferenceAttribute;
import de.factoryfx.factory.exception.RethrowingFactoryExceptionHandler;
import org.junit.Assert;
import org.junit.Test;

public class FactoryManagerLifeCycleTest {

    public static class DummyLifeObejct {
        public final String string;
        public final DummyLifeObejct ref;

        public DummyLifeObejct(String string, DummyLifeObejct ref) {
            this.string = string;
            this.ref = ref;
        }
    }

    public static class LifecycleFactoryBase extends FactoryBase<DummyLifeObejct,Void, LifecycleFactoryA> {
        public List<String> createCalls= new ArrayList<>();
        public List<String> reCreateCalls= new ArrayList<>();
        public List<String> startCalls= new ArrayList<>();
        public List<String> destroyCalls= new ArrayList<>();

        public LifecycleFactoryBase(){
            configLifeCycle().setCreator(() -> {
                createCalls.add("created");
                return new DummyLifeObejct("",null);
            });
            configLifeCycle().setReCreator(dummyLifeObject -> {
                reCreateCalls.add("created");
                return new DummyLifeObejct("",null);
            });
            configLifeCycle().setDestroyer(dummyLifeObject -> destroyCalls.add("created"));
            configLifeCycle().setStarter(dummyLifeObject -> startCalls.add("created"));
        }

        public void resetCounter(){
            createCalls.clear();
            reCreateCalls.clear();
            startCalls.clear();
            destroyCalls.clear();
        }

        public void copyCounters(LifecycleFactoryBase from){
            createCalls.clear();
            reCreateCalls.clear();
            startCalls.clear();
            destroyCalls.clear();

            createCalls.addAll(from.createCalls);
            reCreateCalls.addAll(from.reCreateCalls);
            startCalls.addAll(from.startCalls);
            destroyCalls.addAll(from.destroyCalls);
        }
    }

    public static class LifecycleFactoryA extends LifecycleFactoryBase {
        public final FactoryReferenceAttribute<DummyLifeObejct, LifecycleFactoryB> ref = new FactoryReferenceAttribute<>(LifecycleFactoryB.class);
        public final FactoryReferenceListAttribute<DummyLifeObejct, LifecycleFactoryC> refList = new FactoryReferenceListAttribute<>(LifecycleFactoryC.class);
        public final FactoryReferenceAttribute<DummyLifeObejct, LifecycleFactoryC> refC = new FactoryReferenceAttribute<>(LifecycleFactoryC.class);

        public final FactoryReferenceAttribute<DummyLifeObejct, LifecycleFactoryA> refA = new FactoryReferenceAttribute<>(LifecycleFactoryA.class);
    }

    public static class LifecycleFactoryB extends LifecycleFactoryBase {
        public final FactoryReferenceAttribute<DummyLifeObejct, LifecycleFactoryC> refC = new FactoryReferenceAttribute<>(LifecycleFactoryC.class);
        public StringAttribute stringAttribute=new StringAttribute();

        public final FactoryViewListReferenceAttribute<LifecycleFactoryA,DummyLifeObejct, LifecycleFactoryC> listView = new FactoryViewListReferenceAttribute<LifecycleFactoryA,DummyLifeObejct, LifecycleFactoryC>(
                root -> root.refList).labelText("ExampleA2");
    }

    public static class LifecycleFactoryC extends LifecycleFactoryBase {
        public StringAttribute stringAttribute=new StringAttribute();
    }

    static {
        DataDictionary.getDataDictionary(LifecycleFactoryA.class).setNewCopyInstanceSupplier(lifecycleFactoryA -> {
            LifecycleFactoryA copy = new LifecycleFactoryA();
            copy.copyCounters(lifecycleFactoryA);
            return copy;
        });
        DataDictionary.getDataDictionary(LifecycleFactoryB.class).setNewCopyInstanceSupplier(lifecycleFactoryB -> {
            LifecycleFactoryB copy = new LifecycleFactoryB();
            copy.copyCounters(lifecycleFactoryB);
            return copy;
        });
        DataDictionary.getDataDictionary(LifecycleFactoryC.class).setNewCopyInstanceSupplier(lifecycleFactoryC -> {
            LifecycleFactoryC copy = new LifecycleFactoryC();
            copy.copyCounters(lifecycleFactoryC);
            return copy;
        });
    }



    @Test
    public void test_initial_start(){
        FactoryManager<Void,DummyLifeObejct, LifecycleFactoryA> factoryManager = new FactoryManager<>(new RethrowingFactoryExceptionHandler());

        LifecycleFactoryA exampleFactoryA = new LifecycleFactoryA();
        LifecycleFactoryB exampleFactoryB = new LifecycleFactoryB();
        exampleFactoryA.ref.set(exampleFactoryB);

        exampleFactoryA = exampleFactoryA.internal().addBackReferences();
        exampleFactoryB = exampleFactoryA.ref.get();

        factoryManager.start(new RootFactoryWrapper<>(exampleFactoryA));

        Assert.assertEquals(1,exampleFactoryA.createCalls.size());
        Assert.assertEquals(1,exampleFactoryB.createCalls.size());

        Assert.assertEquals(1,exampleFactoryA.startCalls.size());
        Assert.assertEquals(1,exampleFactoryB.startCalls.size());
    }

    @Test
    public void test_initial_destroy(){
        FactoryManager<Void,DummyLifeObejct, LifecycleFactoryA> factoryManager = new FactoryManager<>(new RethrowingFactoryExceptionHandler());

        LifecycleFactoryA exampleFactoryA = new LifecycleFactoryA();
        LifecycleFactoryB exampleFactoryB = new LifecycleFactoryB();
        exampleFactoryA.ref.set(exampleFactoryB);

        factoryManager.start(new RootFactoryWrapper<>(exampleFactoryA));
        factoryManager.stop();

        Assert.assertEquals(1,exampleFactoryA.destroyCalls.size());
        Assert.assertEquals(1,exampleFactoryB.destroyCalls.size());
    }


    @Test
    public void test_initial_destroy_reused_ref(){
        FactoryManager<Void,DummyLifeObejct, LifecycleFactoryA> factoryManager = new FactoryManager<>(new RethrowingFactoryExceptionHandler());

        LifecycleFactoryA exampleFactoryA = new LifecycleFactoryA();
        LifecycleFactoryB exampleFactoryB = new LifecycleFactoryB();
        LifecycleFactoryC exampleFactoryReused = new LifecycleFactoryC();
        exampleFactoryA.ref.set(exampleFactoryB);

        exampleFactoryB.refC.set(exampleFactoryReused);
        exampleFactoryA.refC.set(exampleFactoryReused);


        factoryManager.start(new RootFactoryWrapper<>(exampleFactoryA));
        factoryManager.stop();

        Assert.assertEquals(1,exampleFactoryReused.destroyCalls.size());
    }

    @Test
    public void test_initial_changed_double_used(){
        FactoryManager<Void,DummyLifeObejct, LifecycleFactoryA> factoryManager = new FactoryManager<>(new RethrowingFactoryExceptionHandler());

        LifecycleFactoryA exampleFactoryA = new LifecycleFactoryA();
        LifecycleFactoryB exampleFactoryB = new LifecycleFactoryB();
        LifecycleFactoryC exampleFactoryC = new LifecycleFactoryC();


        exampleFactoryA.ref.set(exampleFactoryB);
        exampleFactoryB.refC.set(exampleFactoryC);
        exampleFactoryA.refC.set(exampleFactoryC);

        exampleFactoryA = exampleFactoryA.internal().addBackReferences();
        exampleFactoryB = exampleFactoryA.ref.get();
//        exampleFactoryC = exampleFactoryA.refC.get();

        factoryManager.start(new RootFactoryWrapper<>(exampleFactoryA));

        LifecycleFactoryA common = factoryManager.getCurrentFactory().internal().copy();
        LifecycleFactoryA update = factoryManager.getCurrentFactory().internal().copy();
        update.refC.get().stringAttribute.set("changed");

        exampleFactoryA.resetCounter();
        exampleFactoryB.resetCounter();
        factoryManager.update(common, update,(permission)->true);

        Assert.assertEquals(1,exampleFactoryA.reCreateCalls.size());
        Assert.assertEquals(1,exampleFactoryB.reCreateCalls.size());
    }

    @Test
    public void test_initial_changed(){
        FactoryManager<Void,DummyLifeObejct, LifecycleFactoryA> factoryManager = new FactoryManager<>(new RethrowingFactoryExceptionHandler());

        LifecycleFactoryA exampleFactoryA = new LifecycleFactoryA();
        LifecycleFactoryB exampleFactoryB = new LifecycleFactoryB();
        exampleFactoryA.ref.set(exampleFactoryB);

        exampleFactoryA = exampleFactoryA.internal().addBackReferences();
        exampleFactoryB = exampleFactoryA.ref.get();

        factoryManager.start(new RootFactoryWrapper<>(exampleFactoryA));

        LifecycleFactoryA common = factoryManager.getCurrentFactory().internal().copy();
        LifecycleFactoryA update = factoryManager.getCurrentFactory().internal().copy();
        update.ref.get().stringAttribute.set("changed");

        exampleFactoryA.resetCounter();
        exampleFactoryB.resetCounter();
        factoryManager.update(common,update,(permission)->true);

        Assert.assertEquals(1,exampleFactoryA.destroyCalls.size());
        Assert.assertEquals(1,exampleFactoryB.destroyCalls.size());

        Assert.assertEquals(1,exampleFactoryA.reCreateCalls.size());
        Assert.assertEquals(1,exampleFactoryB.reCreateCalls.size());

        Assert.assertEquals(1,exampleFactoryA.startCalls.size());
        Assert.assertEquals(1,exampleFactoryB.startCalls.size());
    }

    @Test
    public void test_initial_viewlist_added(){
        FactoryManager<Void,DummyLifeObejct, LifecycleFactoryA> factoryManager = new FactoryManager<>(new RethrowingFactoryExceptionHandler());

        LifecycleFactoryA exampleFactoryA = new LifecycleFactoryA();
        LifecycleFactoryB exampleFactoryB = new LifecycleFactoryB();
        exampleFactoryA.ref.set(exampleFactoryB);

        exampleFactoryA = exampleFactoryA.internal().addBackReferences();
        exampleFactoryB = exampleFactoryA.ref.get();

        factoryManager.start(new RootFactoryWrapper<>(exampleFactoryA));

        LifecycleFactoryA common = factoryManager.getCurrentFactory().utility().copy();
        LifecycleFactoryA update = factoryManager.getCurrentFactory().utility().copy();
        update.refList.get().add(new LifecycleFactoryC());

        exampleFactoryA.resetCounter();
        exampleFactoryB.resetCounter();

        System.out.println(ObjectMapperBuilder.build().writeValueAsString(update));


        factoryManager.update(common,update,(permission)->true);

        Assert.assertEquals(1,exampleFactoryA.destroyCalls.size());
        Assert.assertEquals(1,exampleFactoryB.destroyCalls.size());

        Assert.assertEquals(1,exampleFactoryA.reCreateCalls.size());
        Assert.assertEquals(1,exampleFactoryB.reCreateCalls.size());

        Assert.assertEquals(1,exampleFactoryA.startCalls.size());
        Assert.assertEquals(1,exampleFactoryB.startCalls.size());
    }

    @Test
    public void test_initial_viewlist_removed(){
        FactoryManager<Void,DummyLifeObejct, LifecycleFactoryA> factoryManager = new FactoryManager<>(new RethrowingFactoryExceptionHandler());

        LifecycleFactoryA exampleFactoryA = new LifecycleFactoryA();
        LifecycleFactoryB exampleFactoryB = new LifecycleFactoryB();
        exampleFactoryA.ref.set(exampleFactoryB);
        exampleFactoryA.refList.get().add(new LifecycleFactoryC());

        exampleFactoryA = exampleFactoryA.internal().addBackReferences();
        exampleFactoryB = exampleFactoryA.ref.get();

        factoryManager.start(new RootFactoryWrapper<>(exampleFactoryA));

        LifecycleFactoryA common = factoryManager.getCurrentFactory().internal().copy();
        LifecycleFactoryA update = factoryManager.getCurrentFactory().internal().copy();
        update.refList.get().clear();

        exampleFactoryA.resetCounter();
        exampleFactoryB.resetCounter();
        factoryManager.update(common,update,(permission)->true);

        Assert.assertEquals(1,exampleFactoryA.destroyCalls.size());
        Assert.assertEquals(1,exampleFactoryB.destroyCalls.size());

        Assert.assertEquals(1,exampleFactoryA.reCreateCalls.size());
        Assert.assertEquals(1,exampleFactoryB.reCreateCalls.size());

        Assert.assertEquals(1,exampleFactoryA.startCalls.size());
        Assert.assertEquals(1,exampleFactoryB.startCalls.size());
    }


    @Test
    public void test_changed_list_only(){
        FactoryManager<Void,DummyLifeObejct, LifecycleFactoryA> factoryManager = new FactoryManager<>(new RethrowingFactoryExceptionHandler());

        LifecycleFactoryA exampleFactoryA = new LifecycleFactoryA();
        exampleFactoryA.refList.add(new LifecycleFactoryC());

        exampleFactoryA = exampleFactoryA.internal().addBackReferences();

        factoryManager.start(new RootFactoryWrapper<>(exampleFactoryA));

        LifecycleFactoryA common = factoryManager.getCurrentFactory().internal().copy();
        LifecycleFactoryA update = factoryManager.getCurrentFactory().internal().copy();
        update.refList.clear();

        exampleFactoryA.resetCounter();
        factoryManager.update(common,update,(permission)->true);

        Assert.assertEquals(1,exampleFactoryA.destroyCalls.size());

        Assert.assertEquals(1,exampleFactoryA.reCreateCalls.size());

        Assert.assertEquals(1,exampleFactoryA.startCalls.size());
    }



    @Test
    public void test_changed_list_only_no_changes(){
        FactoryManager<Void,DummyLifeObejct, LifecycleFactoryA> factoryManager = new FactoryManager<>(new RethrowingFactoryExceptionHandler());

        LifecycleFactoryA root = new LifecycleFactoryA();
        LifecycleFactoryA exampleFactoryA = new LifecycleFactoryA();
        root.refA.set(exampleFactoryA);
        exampleFactoryA.refList.add(new LifecycleFactoryC());

        factoryManager.start(new RootFactoryWrapper<>(root));

        LifecycleFactoryA common = factoryManager.getCurrentFactory().utility().copy();
        LifecycleFactoryA update = factoryManager.getCurrentFactory().utility().copy();
//        update.refC.set(update.refList.get(0));
//        update.refList.remove(0);


        exampleFactoryA.resetCounter();
        factoryManager.update(common,update,(permission)->true);

        Assert.assertEquals(0,exampleFactoryA.destroyCalls.size());

        Assert.assertEquals(0,exampleFactoryA.reCreateCalls.size());

        Assert.assertEquals(0,exampleFactoryA.startCalls.size());
    }

    @Test
    public void test_list_instance_only_once(){
        FactoryManager<Void,DummyLifeObejct, LifecycleFactoryA> factoryManager = new FactoryManager<>(new RethrowingFactoryExceptionHandler());

        LifecycleFactoryA root = new LifecycleFactoryA();
        root.refList.add(new LifecycleFactoryC());
        factoryManager.start(new RootFactoryWrapper<>(root));

        Assert.assertEquals(1,factoryManager.getCurrentFactory().refList.get(0).createCalls.size());

        LifecycleFactoryA common = factoryManager.getCurrentFactory().utility().copy();
        LifecycleFactoryA update = factoryManager.getCurrentFactory().utility().copy();
        update.refList.add(new LifecycleFactoryC());

        factoryManager.update(common,update,(permission)->true);
        Assert.assertEquals(1,factoryManager.getCurrentFactory().refList.get(0).createCalls.size());
        Assert.assertEquals(1,factoryManager.getCurrentFactory().refList.get(1).createCalls.size());

    }
}