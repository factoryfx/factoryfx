package de.factoryfx.factory;

import java.util.ArrayList;
import java.util.List;

import de.factoryfx.data.attribute.types.StringAttribute;
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


    public static class LivecycleFactoryA extends FactoryBase<DummyLifeObejct,Void> {
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

    public static class LivecycleFactoryB extends FactoryBase<DummyLifeObejct,Void> {

        public final FactoryViewListReferenceAttribute<LivecycleFactoryA,DummyLifeObejct,LivecycleFactoryC> listView = new FactoryViewListReferenceAttribute<LivecycleFactoryA,DummyLifeObejct,LivecycleFactoryC>(
                root -> root.refList.get()).labelText("ExampleA2");

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

    public static class LivecycleFactoryC extends SimpleFactoryBase<DummyLifeObejct,Void> {

        public StringAttribute stringAttribute=new StringAttribute();

        @Override
        public DummyLifeObejct createImpl() {
            return new DummyLifeObejct("",null);
        }
    }


    @Test
    public void test_initial_start(){
        FactoryManager<Void,DummyLifeObejct,LivecycleFactoryA> factoryManager = new FactoryManager<>(new RethrowingFactoryExceptionHandler<Void>());

        LivecycleFactoryA exampleFactoryA = new LivecycleFactoryA();
        LivecycleFactoryB exampleFactoryB = new LivecycleFactoryB();
        exampleFactoryA.ref.set(exampleFactoryB);

        exampleFactoryA = exampleFactoryA.internal().prepareUsableCopy();
        exampleFactoryB = exampleFactoryA.ref.get();

        factoryManager.start(exampleFactoryA);

        Assert.assertEquals(1,exampleFactoryA.createCalls.size());
        Assert.assertEquals(1,exampleFactoryB.createCalls.size());

        Assert.assertEquals(1,exampleFactoryA.startCalls.size());
        Assert.assertEquals(1,exampleFactoryB.startCalls.size());
    }

    @Test
    public void test_initial_destroy(){
        FactoryManager<Void,DummyLifeObejct,LivecycleFactoryA> factoryManager = new FactoryManager<>(new RethrowingFactoryExceptionHandler<Void>());

        LivecycleFactoryA exampleFactoryA = new LivecycleFactoryA();
        LivecycleFactoryB exampleFactoryB = new LivecycleFactoryB();
        exampleFactoryA.ref.set(exampleFactoryB);

        exampleFactoryA = exampleFactoryA.internal().prepareUsableCopy();
        exampleFactoryB = exampleFactoryA.ref.get();

        factoryManager.start(exampleFactoryA);
        factoryManager.stop();

        Assert.assertEquals(1,exampleFactoryA.destroyCalls.size());
        Assert.assertEquals(1,exampleFactoryB.destroyCalls.size());
    }

    @Test
    public void test_initial_changed(){
        FactoryManager<Void,DummyLifeObejct,LivecycleFactoryA> factoryManager = new FactoryManager<>(new RethrowingFactoryExceptionHandler<Void>());

        LivecycleFactoryA exampleFactoryA = new LivecycleFactoryA();
        LivecycleFactoryB exampleFactoryB = new LivecycleFactoryB();
        exampleFactoryA.ref.set(exampleFactoryB);

        exampleFactoryA = exampleFactoryA.internal().prepareUsableCopy();
        exampleFactoryB = exampleFactoryA.ref.get();

        factoryManager.start(exampleFactoryA);

        LivecycleFactoryA common = factoryManager.getCurrentFactory().internal().prepareUsableCopy();
        LivecycleFactoryA update = factoryManager.getCurrentFactory().internal().prepareUsableCopy();
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
        FactoryManager<Void,DummyLifeObejct,LivecycleFactoryA> factoryManager = new FactoryManager<>(new RethrowingFactoryExceptionHandler<Void>());

        LivecycleFactoryA exampleFactoryA = new LivecycleFactoryA();
        LivecycleFactoryB exampleFactoryB = new LivecycleFactoryB();
        exampleFactoryA.ref.set(exampleFactoryB);

        exampleFactoryA = exampleFactoryA.internal().prepareUsableCopy();
        exampleFactoryB = exampleFactoryA.ref.get();

        factoryManager.start(exampleFactoryA);

        LivecycleFactoryA common = factoryManager.getCurrentFactory().internal().prepareUsableCopy();
        LivecycleFactoryA update = factoryManager.getCurrentFactory().internal().prepareUsableCopy();
        update.refList.get().add(new LivecycleFactoryC());

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
    public void test_initial_viewlist_removed(){
        FactoryManager<Void,DummyLifeObejct,LivecycleFactoryA> factoryManager = new FactoryManager<>(new RethrowingFactoryExceptionHandler<Void>());

        LivecycleFactoryA exampleFactoryA = new LivecycleFactoryA();
        LivecycleFactoryB exampleFactoryB = new LivecycleFactoryB();
        exampleFactoryA.ref.set(exampleFactoryB);
        exampleFactoryA.refList.get().add(new LivecycleFactoryC());

        exampleFactoryA = exampleFactoryA.internal().prepareUsableCopy();
        exampleFactoryB = exampleFactoryA.ref.get();

        factoryManager.start(exampleFactoryA);

        LivecycleFactoryA common = factoryManager.getCurrentFactory().internal().prepareUsableCopy();
        LivecycleFactoryA update = factoryManager.getCurrentFactory().internal().prepareUsableCopy();
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
        FactoryManager<Void,DummyLifeObejct,LivecycleFactoryA> factoryManager = new FactoryManager<>(new RethrowingFactoryExceptionHandler<Void>());

        LivecycleFactoryA exampleFactoryA = new LivecycleFactoryA();
        exampleFactoryA.refList.add(new LivecycleFactoryC());

        exampleFactoryA = exampleFactoryA.internal().prepareUsableCopy();

        factoryManager.start(exampleFactoryA);

        LivecycleFactoryA common = factoryManager.getCurrentFactory().internal().prepareUsableCopy();
        LivecycleFactoryA update = factoryManager.getCurrentFactory().internal().prepareUsableCopy();
        update.refList.clear();

        exampleFactoryA.resetCounter();
        factoryManager.update(common,update,(permission)->true);

        Assert.assertEquals(1,exampleFactoryA.destroyCalls.size());

        Assert.assertEquals(1,exampleFactoryA.reCreateCalls.size());

        Assert.assertEquals(1,exampleFactoryA.startCalls.size());
    }



    @Test
    public void test_changed_list_only__no_changes(){
        FactoryManager<Void,DummyLifeObejct,LivecycleFactoryA> factoryManager = new FactoryManager<>(new RethrowingFactoryExceptionHandler<Void>());

        LivecycleFactoryA root = new LivecycleFactoryA();
        LivecycleFactoryA exampleFactoryA = new LivecycleFactoryA();
        root.refA.set(exampleFactoryA);
        exampleFactoryA.refList.add(new LivecycleFactoryC());

        root = root.internal().prepareUsableCopy();
        exampleFactoryA = root.refA.get();

        factoryManager.start(exampleFactoryA);

        LivecycleFactoryA common = factoryManager.getCurrentFactory().internal().prepareUsableCopy();
        LivecycleFactoryA update = factoryManager.getCurrentFactory().internal().prepareUsableCopy();
//        update.refC.set(update.refList.get(0));
//        update.refList.remove(0);


        exampleFactoryA.resetCounter();
        factoryManager.update(common,update,(permission)->true);

        Assert.assertEquals(0,exampleFactoryA.destroyCalls.size());

        Assert.assertEquals(0,exampleFactoryA.reCreateCalls.size());

        Assert.assertEquals(0,exampleFactoryA.startCalls.size());
    }
}