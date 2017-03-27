package de.factoryfx.factory;

import java.util.ArrayList;
import java.util.List;

import de.factoryfx.data.attribute.AttributeMetadata;
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


    public static class ExampleFactoryA extends FactoryBase<DummyLifeObejct,Void> {
        public final FactoryReferenceAttribute<DummyLifeObejct,ExampleFactoryB> ref = new FactoryReferenceAttribute<>(ExampleFactoryB.class,new AttributeMetadata());
        public final FactoryReferenceListAttribute<DummyLifeObejct,ExampleFactoryC> refList = new FactoryReferenceListAttribute<>(ExampleFactoryC.class,new AttributeMetadata());
        public final FactoryReferenceAttribute<DummyLifeObejct,ExampleFactoryC> refC = new FactoryReferenceAttribute<>(ExampleFactoryC.class,new AttributeMetadata());

        public final FactoryReferenceAttribute<DummyLifeObejct,ExampleFactoryA> refA = new FactoryReferenceAttribute<>(ExampleFactoryA.class,new AttributeMetadata());



        public List<String> createCalls= new ArrayList<>();
        public List<String> reCreateCalls= new ArrayList<>();
        public List<String> startCalls= new ArrayList<>();
        public List<String> destroyCalls= new ArrayList<>();

        public ExampleFactoryA(){
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

    public static class ExampleFactoryB extends FactoryBase<DummyLifeObejct,Void> {

        public final FactoryViewListReferenceAttribute<ExampleFactoryA,DummyLifeObejct,ExampleFactoryC> listView = new FactoryViewListReferenceAttribute<>(new AttributeMetadata().labelText("ExampleA2"),
                root -> root.refList.get());

        public List<String> createCalls= new ArrayList<>();
        public List<String> reCreateCalls= new ArrayList<>();
        public List<String> startCalls= new ArrayList<>();
        public List<String> destroyCalls= new ArrayList<>();
        public StringAttribute stringAttribute=new StringAttribute(new AttributeMetadata());

        public ExampleFactoryB(){
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

    public static class ExampleFactoryC extends SimpleFactoryBase<DummyLifeObejct,Void> {

        public StringAttribute stringAttribute=new StringAttribute(new AttributeMetadata());

        @Override
        public DummyLifeObejct createImpl() {
            return new DummyLifeObejct("",null);
        }
    }


    @Test
    public void test_initial_start(){
        FactoryManager<DummyLifeObejct,Void,ExampleFactoryA> factoryManager = new FactoryManager<>(new RethrowingFactoryExceptionHandler<Void>());

        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
        ExampleFactoryB exampleFactoryB = new ExampleFactoryB();
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
        FactoryManager<DummyLifeObejct,Void,ExampleFactoryA> factoryManager = new FactoryManager<>(new RethrowingFactoryExceptionHandler<Void>());

        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
        ExampleFactoryB exampleFactoryB = new ExampleFactoryB();
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
        FactoryManager<DummyLifeObejct,Void,ExampleFactoryA> factoryManager = new FactoryManager<>(new RethrowingFactoryExceptionHandler<Void>());

        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
        ExampleFactoryB exampleFactoryB = new ExampleFactoryB();
        exampleFactoryA.ref.set(exampleFactoryB);

        exampleFactoryA = exampleFactoryA.internal().prepareUsableCopy();
        exampleFactoryB = exampleFactoryA.ref.get();

        factoryManager.start(exampleFactoryA);

        ExampleFactoryA common = factoryManager.getCurrentFactory().internal().prepareUsableCopy();
        ExampleFactoryA update = factoryManager.getCurrentFactory().internal().prepareUsableCopy();
        update.ref.get().stringAttribute.set("changed");

        exampleFactoryA.resetCounter();
        exampleFactoryB.resetCounter();
        factoryManager.update(common,update);

        Assert.assertEquals(1,exampleFactoryA.destroyCalls.size());
        Assert.assertEquals(1,exampleFactoryB.destroyCalls.size());

        Assert.assertEquals(1,exampleFactoryA.reCreateCalls.size());
        Assert.assertEquals(1,exampleFactoryB.reCreateCalls.size());

        Assert.assertEquals(1,exampleFactoryA.startCalls.size());
        Assert.assertEquals(1,exampleFactoryB.startCalls.size());
    }

    @Test
    public void test_initial_viewlist_added(){
        FactoryManager<DummyLifeObejct,Void,ExampleFactoryA> factoryManager = new FactoryManager<>(new RethrowingFactoryExceptionHandler<Void>());

        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
        ExampleFactoryB exampleFactoryB = new ExampleFactoryB();
        exampleFactoryA.ref.set(exampleFactoryB);

        exampleFactoryA = exampleFactoryA.internal().prepareUsableCopy();
        exampleFactoryB = exampleFactoryA.ref.get();

        factoryManager.start(exampleFactoryA);

        ExampleFactoryA common = factoryManager.getCurrentFactory().internal().prepareUsableCopy();
        ExampleFactoryA update = factoryManager.getCurrentFactory().internal().prepareUsableCopy();
        update.refList.get().add(new ExampleFactoryC());

        exampleFactoryA.resetCounter();
        exampleFactoryB.resetCounter();
        factoryManager.update(common,update);

        Assert.assertEquals(1,exampleFactoryA.destroyCalls.size());
        Assert.assertEquals(1,exampleFactoryB.destroyCalls.size());

        Assert.assertEquals(1,exampleFactoryA.reCreateCalls.size());
        Assert.assertEquals(1,exampleFactoryB.reCreateCalls.size());

        Assert.assertEquals(1,exampleFactoryA.startCalls.size());
        Assert.assertEquals(1,exampleFactoryB.startCalls.size());
    }

    @Test
    public void test_initial_viewlist_removed(){
        FactoryManager<DummyLifeObejct,Void,ExampleFactoryA> factoryManager = new FactoryManager<>(new RethrowingFactoryExceptionHandler<Void>());

        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
        ExampleFactoryB exampleFactoryB = new ExampleFactoryB();
        exampleFactoryA.ref.set(exampleFactoryB);
        exampleFactoryA.refList.get().add(new ExampleFactoryC());

        exampleFactoryA = exampleFactoryA.internal().prepareUsableCopy();
        exampleFactoryB = exampleFactoryA.ref.get();

        factoryManager.start(exampleFactoryA);

        ExampleFactoryA common = factoryManager.getCurrentFactory().internal().prepareUsableCopy();
        ExampleFactoryA update = factoryManager.getCurrentFactory().internal().prepareUsableCopy();
        update.refList.get().clear();

        exampleFactoryA.resetCounter();
        exampleFactoryB.resetCounter();
        factoryManager.update(common,update);

        Assert.assertEquals(1,exampleFactoryA.destroyCalls.size());
        Assert.assertEquals(1,exampleFactoryB.destroyCalls.size());

        Assert.assertEquals(1,exampleFactoryA.reCreateCalls.size());
        Assert.assertEquals(1,exampleFactoryB.reCreateCalls.size());

        Assert.assertEquals(1,exampleFactoryA.startCalls.size());
        Assert.assertEquals(1,exampleFactoryB.startCalls.size());
    }


    @Test
    public void test_changed_list_only(){
        FactoryManager<DummyLifeObejct,Void,ExampleFactoryA> factoryManager = new FactoryManager<>(new RethrowingFactoryExceptionHandler<Void>());

        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
        exampleFactoryA.refList.add(new ExampleFactoryC());

        exampleFactoryA = exampleFactoryA.internal().prepareUsableCopy();

        factoryManager.start(exampleFactoryA);

        ExampleFactoryA common = factoryManager.getCurrentFactory().internal().prepareUsableCopy();
        ExampleFactoryA update = factoryManager.getCurrentFactory().internal().prepareUsableCopy();
        update.refList.clear();

        exampleFactoryA.resetCounter();
        factoryManager.update(common,update);

        Assert.assertEquals(1,exampleFactoryA.destroyCalls.size());

        Assert.assertEquals(1,exampleFactoryA.reCreateCalls.size());

        Assert.assertEquals(1,exampleFactoryA.startCalls.size());
    }



    @Test
    public void test_changed_list_only__no_changes(){
        FactoryManager<DummyLifeObejct,Void,ExampleFactoryA> factoryManager = new FactoryManager<>(new RethrowingFactoryExceptionHandler<Void>());

        ExampleFactoryA root = new ExampleFactoryA();
        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
        root.refA.set(exampleFactoryA);
        exampleFactoryA.refList.add(new ExampleFactoryC());

        root = root.internal().prepareUsableCopy();
        exampleFactoryA = root.refA.get();

        factoryManager.start(exampleFactoryA);

        ExampleFactoryA common = factoryManager.getCurrentFactory().internal().prepareUsableCopy();
        ExampleFactoryA update = factoryManager.getCurrentFactory().internal().prepareUsableCopy();
//        update.refC.set(update.refList.get(0));
//        update.refList.remove(0);


        exampleFactoryA.resetCounter();
        factoryManager.update(common,update);

        Assert.assertEquals(0,exampleFactoryA.destroyCalls.size());

        Assert.assertEquals(0,exampleFactoryA.reCreateCalls.size());

        Assert.assertEquals(0,exampleFactoryA.startCalls.size());
    }
}