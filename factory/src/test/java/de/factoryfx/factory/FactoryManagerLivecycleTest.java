package de.factoryfx.factory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import de.factoryfx.data.DataDictionary;
import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.factory.atrribute.FactoryReferenceListAttribute;
import de.factoryfx.factory.atrribute.FactoryViewListReferenceAttribute;
import de.factoryfx.factory.exception.RethrowingFactoryExceptionHandler;
import org.junit.Assert;
import org.junit.Test;

public class FactoryManagerLivecycleTest {

    public static class DummyLifeObejct {
        public final String string;
        public final DummyLifeObejct ref;

        public DummyLifeObejct(String string, DummyLifeObejct ref) {
            this.string = string;
            this.ref = ref;
        }
    }

    public static class LivecycleFactoryBase extends FactoryBase<DummyLifeObejct,Void,LivecycleFactoryA> {
        public List<String> createCalls= new ArrayList<>();
        public List<String> reCreateCalls= new ArrayList<>();
        public List<String> startCalls= new ArrayList<>();
        public List<String> destroyCalls= new ArrayList<>();

        public LivecycleFactoryBase(){
            configLiveCycle().setCreator(() -> {
                createCalls.add("created");
                return new DummyLifeObejct("",null);
            });
            configLiveCycle().setReCreator(dummyLifeObject -> {
                reCreateCalls.add("created");
                return new DummyLifeObejct("",null);
            });
            configLiveCycle().setDestroyer(dummyLifeObject -> destroyCalls.add("created"));
            configLiveCycle().setStarter(dummyLifeObject -> startCalls.add("created"));
        }

        public void resetCounter(){
            createCalls.clear();
            reCreateCalls.clear();
            startCalls.clear();
            destroyCalls.clear();
        }

        public void copyCounters(LivecycleFactoryBase from){
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

    public static class LivecycleFactoryA extends LivecycleFactoryBase {
        public final FactoryReferenceAttribute<DummyLifeObejct,LivecycleFactoryB> ref = new FactoryReferenceAttribute<>(LivecycleFactoryB.class);
        public final FactoryReferenceListAttribute<DummyLifeObejct,LivecycleFactoryC> refList = new FactoryReferenceListAttribute<>(LivecycleFactoryC.class);
        public final FactoryReferenceAttribute<DummyLifeObejct,LivecycleFactoryC> refC = new FactoryReferenceAttribute<>(LivecycleFactoryC.class);

        public final FactoryReferenceAttribute<DummyLifeObejct,LivecycleFactoryA> refA = new FactoryReferenceAttribute<>(LivecycleFactoryA.class);
    }

    public static class LivecycleFactoryB extends LivecycleFactoryBase {
        public final FactoryReferenceAttribute<DummyLifeObejct,LivecycleFactoryC> refC = new FactoryReferenceAttribute<>(LivecycleFactoryC.class);
        public StringAttribute stringAttribute=new StringAttribute();

        public final FactoryViewListReferenceAttribute<LivecycleFactoryA,DummyLifeObejct,LivecycleFactoryC> listView = new FactoryViewListReferenceAttribute<LivecycleFactoryA,DummyLifeObejct,LivecycleFactoryC>(
                root -> root.refList).labelText("ExampleA2");
    }

    public static class LivecycleFactoryC extends LivecycleFactoryBase {
        public StringAttribute stringAttribute=new StringAttribute();
    }

    static {
        DataDictionary.getDataDictionary(LivecycleFactoryA.class).setNewCopyInstanceSupplier(livecycleFactoryA -> {
            LivecycleFactoryA copy = new LivecycleFactoryA();
            copy.copyCounters(livecycleFactoryA);
            return copy;
        });
        DataDictionary.getDataDictionary(LivecycleFactoryB.class).setNewCopyInstanceSupplier(livecycleFactoryB -> {
            LivecycleFactoryB copy = new LivecycleFactoryB();
            copy.copyCounters(livecycleFactoryB);
            return copy;
        });
        DataDictionary.getDataDictionary(LivecycleFactoryC.class).setNewCopyInstanceSupplier(livecycleFactoryC -> {
            LivecycleFactoryC copy = new LivecycleFactoryC();
            copy.copyCounters(livecycleFactoryC);
            return copy;
        });
    }



    @Test
    public void test_initial_start(){
        FactoryManager<Void,LivecycleFactoryA> factoryManager = new FactoryManager<>(new RethrowingFactoryExceptionHandler());

        LivecycleFactoryA exampleFactoryA = new LivecycleFactoryA();
        LivecycleFactoryB exampleFactoryB = new LivecycleFactoryB();
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
        FactoryManager<Void,LivecycleFactoryA> factoryManager = new FactoryManager<>(new RethrowingFactoryExceptionHandler());

        LivecycleFactoryA exampleFactoryA = new LivecycleFactoryA();
        LivecycleFactoryB exampleFactoryB = new LivecycleFactoryB();
        exampleFactoryA.ref.set(exampleFactoryB);

        factoryManager.start(new RootFactoryWrapper<>(exampleFactoryA));
        factoryManager.stop();

        Assert.assertEquals(1,exampleFactoryA.destroyCalls.size());
        Assert.assertEquals(1,exampleFactoryB.destroyCalls.size());
    }


    @Test
    public void test_initial_destroy_reused_ref(){
        FactoryManager<Void,LivecycleFactoryA> factoryManager = new FactoryManager<>(new RethrowingFactoryExceptionHandler());

        LivecycleFactoryA exampleFactoryA = new LivecycleFactoryA();
        LivecycleFactoryB exampleFactoryB = new LivecycleFactoryB();
        LivecycleFactoryC exampleFactoryReused = new LivecycleFactoryC();
        exampleFactoryA.ref.set(exampleFactoryB);

        exampleFactoryB.refC.set(exampleFactoryReused);
        exampleFactoryA.refC.set(exampleFactoryReused);


        factoryManager.start(new RootFactoryWrapper<>(exampleFactoryA));
        factoryManager.stop();

        Assert.assertEquals(1,exampleFactoryReused.destroyCalls.size());
    }

    @Test
    public void test_initial_changed_double_used(){
        FactoryManager<Void,LivecycleFactoryA> factoryManager = new FactoryManager<>(new RethrowingFactoryExceptionHandler());

        LivecycleFactoryA exampleFactoryA = new LivecycleFactoryA();
        LivecycleFactoryB exampleFactoryB = new LivecycleFactoryB();
        LivecycleFactoryC exampleFactoryC = new LivecycleFactoryC();


        exampleFactoryA.ref.set(exampleFactoryB);
        exampleFactoryB.refC.set(exampleFactoryC);
        exampleFactoryA.refC.set(exampleFactoryC);

        exampleFactoryA = exampleFactoryA.internal().addBackReferences();
        exampleFactoryB = exampleFactoryA.ref.get();
//        exampleFactoryC = exampleFactoryA.refC.get();

        factoryManager.start(new RootFactoryWrapper<>(exampleFactoryA));

        LivecycleFactoryA common = factoryManager.getCurrentFactory().internal().copy();
        LivecycleFactoryA update = factoryManager.getCurrentFactory().internal().copy();
        update.refC.get().stringAttribute.set("changed");

        exampleFactoryA.resetCounter();
        exampleFactoryB.resetCounter();
        factoryManager.update(common, update,(permission)->true);

        Assert.assertEquals(1,exampleFactoryA.reCreateCalls.size());
        Assert.assertEquals(1,exampleFactoryB.reCreateCalls.size());
    }

    @Test
    public void test_initial_changed(){
        FactoryManager<Void,LivecycleFactoryA> factoryManager = new FactoryManager<>(new RethrowingFactoryExceptionHandler());

        LivecycleFactoryA exampleFactoryA = new LivecycleFactoryA();
        LivecycleFactoryB exampleFactoryB = new LivecycleFactoryB();
        exampleFactoryA.ref.set(exampleFactoryB);

        exampleFactoryA = exampleFactoryA.internal().addBackReferences();
        exampleFactoryB = exampleFactoryA.ref.get();

        factoryManager.start(new RootFactoryWrapper<>(exampleFactoryA));

        LivecycleFactoryA common = factoryManager.getCurrentFactory().internal().copy();
        LivecycleFactoryA update = factoryManager.getCurrentFactory().internal().copy();
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
        FactoryManager<Void,LivecycleFactoryA> factoryManager = new FactoryManager<>(new RethrowingFactoryExceptionHandler());

        LivecycleFactoryA exampleFactoryA = new LivecycleFactoryA();
        LivecycleFactoryB exampleFactoryB = new LivecycleFactoryB();
        exampleFactoryA.ref.set(exampleFactoryB);

        exampleFactoryA = exampleFactoryA.internal().addBackReferences();
        exampleFactoryB = exampleFactoryA.ref.get();

        factoryManager.start(new RootFactoryWrapper<>(exampleFactoryA));

        LivecycleFactoryA common = factoryManager.getCurrentFactory().utility().copy();
        LivecycleFactoryA update = factoryManager.getCurrentFactory().utility().copy();
        update.refList.get().add(new LivecycleFactoryC());

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
        FactoryManager<Void,LivecycleFactoryA> factoryManager = new FactoryManager<>(new RethrowingFactoryExceptionHandler());

        LivecycleFactoryA exampleFactoryA = new LivecycleFactoryA();
        LivecycleFactoryB exampleFactoryB = new LivecycleFactoryB();
        exampleFactoryA.ref.set(exampleFactoryB);
        exampleFactoryA.refList.get().add(new LivecycleFactoryC());

        exampleFactoryA = exampleFactoryA.internal().addBackReferences();
        exampleFactoryB = exampleFactoryA.ref.get();

        factoryManager.start(new RootFactoryWrapper<>(exampleFactoryA));

        LivecycleFactoryA common = factoryManager.getCurrentFactory().internal().copy();
        LivecycleFactoryA update = factoryManager.getCurrentFactory().internal().copy();
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
        FactoryManager<Void,LivecycleFactoryA> factoryManager = new FactoryManager<>(new RethrowingFactoryExceptionHandler());

        LivecycleFactoryA exampleFactoryA = new LivecycleFactoryA();
        exampleFactoryA.refList.add(new LivecycleFactoryC());

        exampleFactoryA = exampleFactoryA.internal().addBackReferences();

        factoryManager.start(new RootFactoryWrapper<>(exampleFactoryA));

        LivecycleFactoryA common = factoryManager.getCurrentFactory().internal().copy();
        LivecycleFactoryA update = factoryManager.getCurrentFactory().internal().copy();
        update.refList.clear();

        exampleFactoryA.resetCounter();
        factoryManager.update(common,update,(permission)->true);

        Assert.assertEquals(1,exampleFactoryA.destroyCalls.size());

        Assert.assertEquals(1,exampleFactoryA.reCreateCalls.size());

        Assert.assertEquals(1,exampleFactoryA.startCalls.size());
    }



    @Test
    public void test_changed_list_only_no_changes(){
        FactoryManager<Void,LivecycleFactoryA> factoryManager = new FactoryManager<>(new RethrowingFactoryExceptionHandler());

        LivecycleFactoryA root = new LivecycleFactoryA();
        LivecycleFactoryA exampleFactoryA = new LivecycleFactoryA();
        root.refA.set(exampleFactoryA);
        exampleFactoryA.refList.add(new LivecycleFactoryC());

        factoryManager.start(new RootFactoryWrapper<>(root));

        LivecycleFactoryA common = factoryManager.getCurrentFactory().utility().copy();
        LivecycleFactoryA update = factoryManager.getCurrentFactory().utility().copy();
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
        FactoryManager<Void,LivecycleFactoryA> factoryManager = new FactoryManager<>(new RethrowingFactoryExceptionHandler());

        LivecycleFactoryA root = new LivecycleFactoryA();
        root.refList.add(new LivecycleFactoryC());
        factoryManager.start(new RootFactoryWrapper<>(root));

        Assert.assertEquals(1,factoryManager.getCurrentFactory().refList.get(0).createCalls.size());

        LivecycleFactoryA common = factoryManager.getCurrentFactory().utility().copy();
        LivecycleFactoryA update = factoryManager.getCurrentFactory().utility().copy();
        update.refList.add(new LivecycleFactoryC());

        factoryManager.update(common,update,(permission)->true);
        Assert.assertEquals(1,factoryManager.getCurrentFactory().refList.get(0).createCalls.size());
        Assert.assertEquals(1,factoryManager.getCurrentFactory().refList.get(1).createCalls.size());

    }
}