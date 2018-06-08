package de.factoryfx.factory;

import java.util.ArrayList;
import java.util.List;

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


    public static class LivecycleFactoryA extends FactoryBase<DummyLifeObejct,Void,LivecycleFactoryA> {
        public final FactoryReferenceAttribute<DummyLifeObejct,LivecycleFactoryB> ref = new FactoryReferenceAttribute<>(LivecycleFactoryB.class);
        public final FactoryReferenceListAttribute<DummyLifeObejct,LivecycleFactoryC> refList = new FactoryReferenceListAttribute<>(LivecycleFactoryC.class);
        public final FactoryReferenceAttribute<DummyLifeObejct,LivecycleFactoryC> refC = new FactoryReferenceAttribute<>(LivecycleFactoryC.class);

        public final FactoryReferenceAttribute<DummyLifeObejct,LivecycleFactoryA> refA = new FactoryReferenceAttribute<>(LivecycleFactoryA.class);



        public List<String> createCalls= new ArrayList<>();
        public List<String> reCreateCalls= new ArrayList<>();
        public List<String> startCalls= new ArrayList<>();
        public List<String> destroyCalls= new ArrayList<>();

        public LivecycleFactoryA(){
            configLiveCycle().setCreator(() -> {
                createCalls.add("created");
                return new DummyLifeObejct("",null);
            });
            configLiveCycle().setReCreator(dummyLifeObejct -> {
                reCreateCalls.add("created");
                return new DummyLifeObejct("",null);
            });
            configLiveCycle().setDestroyer(dummyLifeObejct -> destroyCalls.add("created"));
            configLiveCycle().setStarter(dummyLifeObejct -> startCalls.add("created"));
        }

        public void resetCounter(){
            createCalls.clear();
            reCreateCalls.clear();
            startCalls.clear();
            destroyCalls.clear();
        }
    }

    public static class LivecycleFactoryB extends FactoryBase<DummyLifeObejct,Void,LivecycleFactoryA> {

        public final FactoryReferenceAttribute<DummyLifeObejct,LivecycleFactoryC> refC = new FactoryReferenceAttribute<>(LivecycleFactoryC.class);

        public final FactoryViewListReferenceAttribute<LivecycleFactoryA,DummyLifeObejct,LivecycleFactoryC> listView = new FactoryViewListReferenceAttribute<LivecycleFactoryA,DummyLifeObejct,LivecycleFactoryC>(
                root -> root.refList).labelText("ExampleA2");

        public List<String> createCalls= new ArrayList<>();
        public List<String> reCreateCalls= new ArrayList<>();
        public List<String> startCalls= new ArrayList<>();
        public List<String> destroyCalls= new ArrayList<>();
        public StringAttribute stringAttribute=new StringAttribute();

        public LivecycleFactoryB(){
            configLiveCycle().setCreator(() -> {
                createCalls.add("created");
                return new DummyLifeObejct("",null);
            });
            configLiveCycle().setReCreator(dummyLifeObejct -> {
                reCreateCalls.add("created");
                return dummyLifeObejct;
            });
            configLiveCycle().setDestroyer(dummyLifeObejct -> destroyCalls.add("created"));
            configLiveCycle().setStarter(dummyLifeObejct -> startCalls.add("created"));
        }

        public void resetCounter(){
            createCalls.clear();
            reCreateCalls.clear();
            startCalls.clear();
            destroyCalls.clear();
        }
    }

    public static class LivecycleFactoryC extends FactoryBase<DummyLifeObejct,Void,LivecycleFactoryA> {

        public List<String> createCalls= new ArrayList<>();
        public List<String> reCreateCalls= new ArrayList<>();
        public List<String> startCalls= new ArrayList<>();
        public List<String> destroyCalls= new ArrayList<>();

        public StringAttribute stringAttribute=new StringAttribute();

        public void resetCounter(){
            createCalls.clear();
            reCreateCalls.clear();
            startCalls.clear();
            destroyCalls.clear();
        }

        public LivecycleFactoryC(){
            configLiveCycle().setCreator(() -> {
                createCalls.add("created");
                return new DummyLifeObejct("",null);
            });
            configLiveCycle().setReCreator(dummyLifeObejct -> {
                reCreateCalls.add("created");
                return new DummyLifeObejct("",null);
            });
            configLiveCycle().setDestroyer(dummyLifeObejct -> destroyCalls.add("created"));
            configLiveCycle().setStarter(dummyLifeObejct -> startCalls.add("created"));
        }
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

        root = root.internal().addBackReferences();
        exampleFactoryA = root.refA.get();

        factoryManager.start(new RootFactoryWrapper<>(exampleFactoryA));

        LivecycleFactoryA common = factoryManager.getCurrentFactory().internal().addBackReferences();
        LivecycleFactoryA update = factoryManager.getCurrentFactory().internal().addBackReferences();
//        update.refC.set(update.refList.get(0));
//        update.refList.remove(0);


        exampleFactoryA.resetCounter();
        factoryManager.update(common,update,(permission)->true);

        Assert.assertEquals(0,exampleFactoryA.destroyCalls.size());

        Assert.assertEquals(0,exampleFactoryA.reCreateCalls.size());

        Assert.assertEquals(0,exampleFactoryA.startCalls.size());
    }
}