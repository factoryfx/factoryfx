package io.github.factoryfx.factory;

import java.util.ArrayList;
import java.util.List;

import ch.qos.logback.classic.Level;
import io.github.factoryfx.factory.attribute.types.StringAttribute;
import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;
import io.github.factoryfx.factory.attribute.dependency.FactoryListAttribute;
import io.github.factoryfx.factory.attribute.dependency.FactoryViewListAttribute;
import io.github.factoryfx.factory.exception.RethrowingFactoryExceptionHandler;
import io.github.factoryfx.factory.metadata.FactoryMetadataManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FactoryManagerLifeCycleTest {

    public static class DummyLifeObject {
        public final String string;
        public final DummyLifeObject ref;

        public DummyLifeObject(String string, DummyLifeObject ref) {
            this.string = string;
            this.ref = ref;
        }
    }

    public static class LifecycleFactoryBase extends FactoryBase<DummyLifeObject, LifecycleFactoryA> {
        public List<String> createCalls= new ArrayList<>();
        public List<String> reCreateCalls= new ArrayList<>();
        public List<String> startCalls= new ArrayList<>();
        public List<String> destroyCalls= new ArrayList<>();

        public LifecycleFactoryBase(){
            configLifeCycle().setCreator(() -> {
                createCalls.add("created");
                return new DummyLifeObject("",null);
            });
            configLifeCycle().setReCreator(dummyLifeObject -> {
                reCreateCalls.add("created");
                return new DummyLifeObject("",null);
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
        public final FactoryAttribute<DummyLifeObject, LifecycleFactoryB> ref = new FactoryAttribute<>();
        public final FactoryListAttribute<DummyLifeObject, LifecycleFactoryC> refList = new FactoryListAttribute<>();
        public final FactoryAttribute<DummyLifeObject, LifecycleFactoryC> refC = new FactoryAttribute<>();

        public final FactoryAttribute<DummyLifeObject, LifecycleFactoryA> refA = new FactoryAttribute<>();

        public final FactoryAttribute<DummyLifeObject, LifecycleFactoryB> ref2 = new FactoryAttribute<>();
    }

    public static class LifecycleFactoryB extends LifecycleFactoryBase {
        public final FactoryAttribute<DummyLifeObject, LifecycleFactoryC> refC = new FactoryAttribute<>();
        public final FactoryAttribute<DummyLifeObject, LifecycleFactoryD> refD = new FactoryAttribute<>();
        public StringAttribute stringAttribute=new StringAttribute();

        public final FactoryViewListAttribute<LifecycleFactoryA, DummyLifeObject, LifecycleFactoryC> listView = new FactoryViewListAttribute<LifecycleFactoryA, DummyLifeObject, LifecycleFactoryC>(
                root -> root.refList).labelText("ExampleA2");
    }

    public static class LifecycleFactoryC extends LifecycleFactoryBase {
        public StringAttribute stringAttribute=new StringAttribute();
    }

    public static class LifecycleFactoryD extends LifecycleFactoryBase {
        public final FactoryAttribute<DummyLifeObject, LifecycleFactoryC> refC = new FactoryAttribute<>();
    }


    static {
        FactoryMetadataManager.getMetadata(LifecycleFactoryA.class).setNewCopyInstanceSupplier(lifecycleFactoryA -> {
            LifecycleFactoryA copy = new LifecycleFactoryA();
            copy.copyCounters(lifecycleFactoryA);
            return copy;
        });
        FactoryMetadataManager.getMetadata(LifecycleFactoryB.class).setNewCopyInstanceSupplier(lifecycleFactoryB -> {
            LifecycleFactoryB copy = new LifecycleFactoryB();
            copy.copyCounters(lifecycleFactoryB);
            return copy;
        });
        FactoryMetadataManager.getMetadata(LifecycleFactoryC.class).setNewCopyInstanceSupplier(lifecycleFactoryC -> {
            LifecycleFactoryC copy = new LifecycleFactoryC();
            copy.copyCounters(lifecycleFactoryC);
            return copy;
        });
    }



    @Test
    public void test_initial_start(){
        FactoryManager<DummyLifeObject, LifecycleFactoryA> factoryManager = new FactoryManager<DummyLifeObject, LifecycleFactoryA>(new RethrowingFactoryExceptionHandler<>());

        LifecycleFactoryA exampleFactoryA = new LifecycleFactoryA();
        LifecycleFactoryB exampleFactoryB = new LifecycleFactoryB();
        exampleFactoryA.ref.set(exampleFactoryB);

        exampleFactoryA = exampleFactoryA.internal().finalise();
        exampleFactoryB = exampleFactoryA.ref.get();

        factoryManager.start(new RootFactoryWrapper<>(exampleFactoryA));

        Assertions.assertEquals(1,exampleFactoryA.createCalls.size());
        Assertions.assertEquals(1,exampleFactoryB.createCalls.size());

        Assertions.assertEquals(1,exampleFactoryA.startCalls.size());
        Assertions.assertEquals(1,exampleFactoryB.startCalls.size());
    }

    @Test
    public void test_initial_destroy(){
        FactoryManager<DummyLifeObject, LifecycleFactoryA> factoryManager = new FactoryManager<DummyLifeObject, LifecycleFactoryA>(new RethrowingFactoryExceptionHandler<>());

        LifecycleFactoryA exampleFactoryA = new LifecycleFactoryA();
        LifecycleFactoryB exampleFactoryB = new LifecycleFactoryB();
        exampleFactoryA.ref.set(exampleFactoryB);

        factoryManager.start(new RootFactoryWrapper<>(exampleFactoryA));
        factoryManager.stop();

        Assertions.assertEquals(1,exampleFactoryA.destroyCalls.size());
        Assertions.assertEquals(1,exampleFactoryB.destroyCalls.size());
    }


    @Test
    public void test_initial_destroy_reused_ref(){
        FactoryManager<DummyLifeObject, LifecycleFactoryA> factoryManager = new FactoryManager<DummyLifeObject, LifecycleFactoryA>(new RethrowingFactoryExceptionHandler<>());

        LifecycleFactoryA exampleFactoryA = new LifecycleFactoryA();
        LifecycleFactoryB exampleFactoryB = new LifecycleFactoryB();
        LifecycleFactoryC exampleFactoryReused = new LifecycleFactoryC();
        exampleFactoryA.ref.set(exampleFactoryB);

        exampleFactoryB.refC.set(exampleFactoryReused);
        exampleFactoryA.refC.set(exampleFactoryReused);


        factoryManager.start(new RootFactoryWrapper<>(exampleFactoryA));
        factoryManager.stop();

        Assertions.assertEquals(1,exampleFactoryReused.destroyCalls.size());
    }

    @Test
    public void test_initial_changed_double_used(){
        FactoryManager<DummyLifeObject, LifecycleFactoryA> factoryManager = new FactoryManager<DummyLifeObject, LifecycleFactoryA>(new RethrowingFactoryExceptionHandler<>());

        LifecycleFactoryA exampleFactoryA = new LifecycleFactoryA();
        LifecycleFactoryB exampleFactoryB = new LifecycleFactoryB();
        LifecycleFactoryC exampleFactoryC = new LifecycleFactoryC();


        exampleFactoryA.ref.set(exampleFactoryB);
        exampleFactoryB.refC.set(exampleFactoryC);
        exampleFactoryA.refC.set(exampleFactoryC);

        exampleFactoryA = exampleFactoryA.internal().finalise();
        exampleFactoryB = exampleFactoryA.ref.get();
//        exampleFactoryC = exampleFactoryA.refC.get();

        factoryManager.start(new RootFactoryWrapper<>(exampleFactoryA));

        LifecycleFactoryA common = factoryManager.getCurrentFactory().internal().copy();
        LifecycleFactoryA update = factoryManager.getCurrentFactory().internal().copy();
        update.refC.get().stringAttribute.set("changed");

        exampleFactoryA.resetCounter();
        exampleFactoryB.resetCounter();
        factoryManager.update(common, update,(permission)->true);

        Assertions.assertEquals(1,exampleFactoryA.reCreateCalls.size());
        Assertions.assertEquals(1,exampleFactoryB.reCreateCalls.size());
    }

    @Test
    public void test_initial_changed(){
        FactoryManager<DummyLifeObject, LifecycleFactoryA> factoryManager = new FactoryManager<DummyLifeObject, LifecycleFactoryA>(new RethrowingFactoryExceptionHandler<>());

        LifecycleFactoryA exampleFactoryA = new LifecycleFactoryA();
        LifecycleFactoryB exampleFactoryB = new LifecycleFactoryB();
        exampleFactoryA.ref.set(exampleFactoryB);

        exampleFactoryA = exampleFactoryA.internal().finalise();
        exampleFactoryB = exampleFactoryA.ref.get();

        factoryManager.start(new RootFactoryWrapper<>(exampleFactoryA));

        LifecycleFactoryA common = factoryManager.getCurrentFactory().internal().copy();
        LifecycleFactoryA update = factoryManager.getCurrentFactory().internal().copy();
        update.ref.get().stringAttribute.set("changed");

        exampleFactoryA.resetCounter();
        exampleFactoryB.resetCounter();
        factoryManager.update(common,update,(permission)->true);

        Assertions.assertEquals(1,exampleFactoryA.destroyCalls.size());
        Assertions.assertEquals(1,exampleFactoryB.destroyCalls.size());

        Assertions.assertEquals(1,exampleFactoryA.reCreateCalls.size());
        Assertions.assertEquals(1,exampleFactoryB.reCreateCalls.size());

        Assertions.assertEquals(1,exampleFactoryA.startCalls.size());
        Assertions.assertEquals(1,exampleFactoryB.startCalls.size());
    }

    @Test
    public void test_initial_viewlist_added(){
        FactoryManager<DummyLifeObject, LifecycleFactoryA> factoryManager = new FactoryManager<DummyLifeObject, LifecycleFactoryA>(new RethrowingFactoryExceptionHandler<>());

        LifecycleFactoryA exampleFactoryA = new LifecycleFactoryA();
        LifecycleFactoryB exampleFactoryB = new LifecycleFactoryB();
        exampleFactoryA.ref.set(exampleFactoryB);

        exampleFactoryA = exampleFactoryA.internal().finalise();
        exampleFactoryB = exampleFactoryA.ref.get();

        factoryManager.start(new RootFactoryWrapper<>(exampleFactoryA));

        LifecycleFactoryA common = factoryManager.getCurrentFactory().utility().copy();
        LifecycleFactoryA update = factoryManager.getCurrentFactory().utility().copy();
        update.refList.get().add(new LifecycleFactoryC());

        exampleFactoryA.resetCounter();
        exampleFactoryB.resetCounter();

        System.out.println(ObjectMapperBuilder.build().writeValueAsString(update));


        factoryManager.update(common,update,(permission)->true);

        Assertions.assertEquals(1,exampleFactoryA.destroyCalls.size());
        Assertions.assertEquals(1,exampleFactoryB.destroyCalls.size());

        Assertions.assertEquals(1,exampleFactoryA.reCreateCalls.size());
        Assertions.assertEquals(1,exampleFactoryB.reCreateCalls.size());

        Assertions.assertEquals(1,exampleFactoryA.startCalls.size());
        Assertions.assertEquals(1,exampleFactoryB.startCalls.size());
    }

    @Test
    public void test_initial_viewlist_removed(){
        FactoryManager<DummyLifeObject, LifecycleFactoryA> factoryManager = new FactoryManager<DummyLifeObject, LifecycleFactoryA>(new RethrowingFactoryExceptionHandler<>());

        LifecycleFactoryA exampleFactoryA = new LifecycleFactoryA();
        LifecycleFactoryB exampleFactoryB = new LifecycleFactoryB();
        exampleFactoryA.ref.set(exampleFactoryB);
        exampleFactoryA.refList.get().add(new LifecycleFactoryC());

        exampleFactoryA = exampleFactoryA.internal().finalise();
        exampleFactoryB = exampleFactoryA.ref.get();

        factoryManager.start(new RootFactoryWrapper<>(exampleFactoryA));

        LifecycleFactoryA common = factoryManager.getCurrentFactory().internal().copy();
        LifecycleFactoryA update = factoryManager.getCurrentFactory().internal().copy();
        update.refList.get().clear();

        exampleFactoryA.resetCounter();
        exampleFactoryB.resetCounter();
        factoryManager.update(common,update,(permission)->true);

        Assertions.assertEquals(1,exampleFactoryA.destroyCalls.size());
        Assertions.assertEquals(1,exampleFactoryB.destroyCalls.size());

        Assertions.assertEquals(1,exampleFactoryA.reCreateCalls.size());
        Assertions.assertEquals(1,exampleFactoryB.reCreateCalls.size());

        Assertions.assertEquals(1,exampleFactoryA.startCalls.size());
        Assertions.assertEquals(1,exampleFactoryB.startCalls.size());
    }


    @Test
    public void test_changed_list_only(){
        FactoryManager<DummyLifeObject, LifecycleFactoryA> factoryManager = new FactoryManager<DummyLifeObject, LifecycleFactoryA>(new RethrowingFactoryExceptionHandler<>());

        LifecycleFactoryA exampleFactoryA = new LifecycleFactoryA();
        exampleFactoryA.refList.add(new LifecycleFactoryC());

        exampleFactoryA = exampleFactoryA.internal().finalise();

        factoryManager.start(new RootFactoryWrapper<>(exampleFactoryA));

        LifecycleFactoryA common = factoryManager.getCurrentFactory().internal().copy();
        LifecycleFactoryA update = factoryManager.getCurrentFactory().internal().copy();
        update.refList.clear();

        exampleFactoryA.resetCounter();
        factoryManager.update(common,update,(permission)->true);

        Assertions.assertEquals(1,exampleFactoryA.destroyCalls.size());
        Assertions.assertEquals(1,exampleFactoryA.reCreateCalls.size());
        Assertions.assertEquals(1,exampleFactoryA.startCalls.size());
    }

    @Test
    public void test_changed_list_only_no_changes(){
        FactoryManager<DummyLifeObject, LifecycleFactoryA> factoryManager = new FactoryManager<DummyLifeObject, LifecycleFactoryA>(new RethrowingFactoryExceptionHandler<>());

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

        Assertions.assertEquals(0,exampleFactoryA.destroyCalls.size());

        Assertions.assertEquals(0,exampleFactoryA.reCreateCalls.size());

        Assertions.assertEquals(0,exampleFactoryA.startCalls.size());
    }

    @Test
    public void test_list_instance_only_once(){
        FactoryManager<DummyLifeObject, LifecycleFactoryA> factoryManager = new FactoryManager<DummyLifeObject, LifecycleFactoryA>(new RethrowingFactoryExceptionHandler<>());

        LifecycleFactoryA root = new LifecycleFactoryA();
        root.refList.add(new LifecycleFactoryC());
        factoryManager.start(new RootFactoryWrapper<>(root));

        Assertions.assertEquals(1,factoryManager.getCurrentFactory().refList.get(0).createCalls.size());

        LifecycleFactoryA common = factoryManager.getCurrentFactory().utility().copy();
        LifecycleFactoryA update = factoryManager.getCurrentFactory().utility().copy();
        update.refList.add(new LifecycleFactoryC());

        factoryManager.update(common,update,(permission)->true);
        Assertions.assertEquals(1,factoryManager.getCurrentFactory().refList.get(0).createCalls.size());
        Assertions.assertEquals(1,factoryManager.getCurrentFactory().refList.get(1).createCalls.size());
    }

    @Test
    public void test_double_used(){
        ch.qos.logback.classic.Logger rootLogger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        rootLogger.setLevel(Level.INFO);

        FactoryManager<DummyLifeObject, LifecycleFactoryA> factoryManager = new FactoryManager<DummyLifeObject, LifecycleFactoryA>(new RethrowingFactoryExceptionHandler<>());

        LifecycleFactoryA root = new LifecycleFactoryA();
        {
            LifecycleFactoryB exampleFactoryB = new LifecycleFactoryB();
            root.ref.set(exampleFactoryB);
        }
        {
            LifecycleFactoryB exampleFactoryB = new LifecycleFactoryB();
            root.ref2.set(exampleFactoryB);
        }
        LifecycleFactoryC lifecycleFactoryC = new LifecycleFactoryC();
        root.ref.get().refC.set(lifecycleFactoryC);
        root.ref2.get().refC.set(lifecycleFactoryC);


        factoryManager.start(new RootFactoryWrapper<>(root));

        LifecycleFactoryA common = factoryManager.getCurrentFactory().utility().copy();
        LifecycleFactoryA update = factoryManager.getCurrentFactory().utility().copy();

        update.ref.get().refC.get().stringAttribute.set("hgfghfhg");


        root.internal().collectChildrenDeep().forEach(data -> ((LifecycleFactoryBase)data).resetCounter());
        factoryManager.update(common,update,(permission)->true);

        Assertions.assertEquals(1,root.reCreateCalls.size());
        Assertions.assertEquals(1,root.ref.get().reCreateCalls.size());
        Assertions.assertEquals(1,root.ref2.get().reCreateCalls.size());
    }


    @Test
    public void test_double_used_more_nested(){
        ch.qos.logback.classic.Logger rootLogger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        rootLogger.setLevel(Level.INFO);

        FactoryManager<DummyLifeObject, LifecycleFactoryA> factoryManager = new FactoryManager<DummyLifeObject, LifecycleFactoryA>(new RethrowingFactoryExceptionHandler<>());

        LifecycleFactoryC lifecycleFactoryC = new LifecycleFactoryC();

        LifecycleFactoryA root = new LifecycleFactoryA();
        {
            LifecycleFactoryB exampleFactoryB = new LifecycleFactoryB();
            root.ref.set(exampleFactoryB);
            LifecycleFactoryD lifecycleFactoryD = new LifecycleFactoryD();
            exampleFactoryB.refD.set(lifecycleFactoryD);
            lifecycleFactoryD.refC.set(lifecycleFactoryC);
        }
        {
            LifecycleFactoryB exampleFactoryB = new LifecycleFactoryB();
            root.ref2.set(exampleFactoryB);
            LifecycleFactoryD lifecycleFactoryD = new LifecycleFactoryD();
            exampleFactoryB.refD.set(lifecycleFactoryD);
            lifecycleFactoryD.refC.set(lifecycleFactoryC);
        }

        factoryManager.start(new RootFactoryWrapper<>(root));

        LifecycleFactoryA common = factoryManager.getCurrentFactory().utility().copy();
        LifecycleFactoryA update = factoryManager.getCurrentFactory().utility().copy();

        update.ref.get().refD.get().refC.get().stringAttribute.set("hgfghfhg");


        root.internal().collectChildrenDeep().forEach(data -> ((LifecycleFactoryBase)data).resetCounter());
        factoryManager.update(common,update,(permission)->true);

        Assertions.assertEquals(1,root.reCreateCalls.size());
        Assertions.assertEquals(1,root.ref.get().reCreateCalls.size());
        Assertions.assertEquals(1,root.ref2.get().reCreateCalls.size());

        Assertions.assertEquals(1,root.ref.get().refD.get().reCreateCalls.size());
        Assertions.assertEquals(1,root.ref2.get().refD.get().reCreateCalls.size());

    }



    @Test
    public void test_double_used_and_once_removed(){
        ch.qos.logback.classic.Logger rootLogger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        rootLogger.setLevel(Level.INFO);

        FactoryManager<DummyLifeObject, LifecycleFactoryA> factoryManager = new FactoryManager<DummyLifeObject, LifecycleFactoryA>(new RethrowingFactoryExceptionHandler<>());

        LifecycleFactoryA root = new LifecycleFactoryA();
        {
            LifecycleFactoryB exampleFactoryB = new LifecycleFactoryB();
            root.ref.set(exampleFactoryB);
        }
        {
            LifecycleFactoryB exampleFactoryB = new LifecycleFactoryB();
            root.ref2.set(exampleFactoryB);
        }
        LifecycleFactoryC lifecycleFactoryC = new LifecycleFactoryC();
        root.ref.get().refC.set(lifecycleFactoryC);
        root.ref2.get().refC.set(lifecycleFactoryC);


        factoryManager.start(new RootFactoryWrapper<>(root));

        LifecycleFactoryA common = factoryManager.getCurrentFactory().utility().copy();
        LifecycleFactoryA update = factoryManager.getCurrentFactory().utility().copy();

        update.ref.get().refC.set(null);


        root.internal().collectChildrenDeep().forEach(data -> ((LifecycleFactoryBase)data).resetCounter());
        factoryManager.update(common,update,(permission)->true);

        Assertions.assertNull(factoryManager.getCurrentFactory().ref.get().refC.get());
        Assertions.assertNotNull(factoryManager.getCurrentFactory().ref2.get().refC.get());

        Assertions.assertEquals(1,root.reCreateCalls.size());
        Assertions.assertEquals(1,root.ref.get().reCreateCalls.size());
        Assertions.assertEquals(0,root.ref2.get().reCreateCalls.size());
    }

    @Test
    public void test_double_remove(){
        ch.qos.logback.classic.Logger rootLogger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        rootLogger.setLevel(Level.INFO);

        FactoryManager<DummyLifeObject, LifecycleFactoryA> factoryManager = new FactoryManager<DummyLifeObject, LifecycleFactoryA>(new RethrowingFactoryExceptionHandler<>());

        LifecycleFactoryC lifecycleFactoryC = new LifecycleFactoryC();

        LifecycleFactoryA root = new LifecycleFactoryA();
        {
            LifecycleFactoryB exampleFactoryB = new LifecycleFactoryB();
            root.ref.set(exampleFactoryB);
            LifecycleFactoryD lifecycleFactoryD = new LifecycleFactoryD();
            exampleFactoryB.refD.set(lifecycleFactoryD);
            lifecycleFactoryD.refC.set(lifecycleFactoryC);
        }
        {
            LifecycleFactoryB exampleFactoryB = new LifecycleFactoryB();
            root.ref2.set(exampleFactoryB);
            LifecycleFactoryD lifecycleFactoryD = new LifecycleFactoryD();
            exampleFactoryB.refD.set(lifecycleFactoryD);
            lifecycleFactoryD.refC.set(lifecycleFactoryC);
        }

        factoryManager.start(new RootFactoryWrapper<>(root));

        LifecycleFactoryA common = factoryManager.getCurrentFactory().utility().copy();
        LifecycleFactoryA update = factoryManager.getCurrentFactory().utility().copy();

        update.ref.get().refD.get().refC.set(null);


        root.internal().collectChildrenDeep().forEach(data -> ((LifecycleFactoryBase)data).resetCounter());
        factoryManager.update(common,update,(permission)->true);

        Assertions.assertEquals(1,root.reCreateCalls.size());
        Assertions.assertEquals(1,root.ref.get().reCreateCalls.size());
        Assertions.assertEquals(0,root.ref2.get().reCreateCalls.size());

        Assertions.assertEquals(1,root.ref.get().refD.get().reCreateCalls.size());
        Assertions.assertEquals(0,root.ref2.get().refD.get().reCreateCalls.size());

    }

    public static class UpdateFactory extends FactoryBase<DummyLifeObject,UpdateFactory> {

        public List<String> updateCalls= new ArrayList<>();
        public List<String> startCalls= new ArrayList<>();
        public List<String> destroyCalls= new ArrayList<>();

        public StringAttribute stringAttribute=new StringAttribute();

        public UpdateFactory(){
            configLifeCycle().setCreator(() -> new DummyLifeObject("",null));
            configLifeCycle().setUpdater(dummyLifeObject -> updateCalls.add("updated"));
            configLifeCycle().setStarter(dummyLifeObject -> startCalls.add("started"));
            configLifeCycle().setDestroyer(dummyLifeObject -> destroyCalls.add("destroy"));
        }

        public void resetCounter(){
            updateCalls.clear();
            startCalls.clear();
            destroyCalls.clear();
        }

        public void copyCounters(UpdateFactory from){
            updateCalls.addAll(from.updateCalls);
            startCalls.addAll(from.startCalls);
            destroyCalls.addAll(from.startCalls);
        }
    }


    @Test
    public void test_updater(){
        ch.qos.logback.classic.Logger rootLogger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        rootLogger.setLevel(Level.INFO);

        FactoryManager<DummyLifeObject,UpdateFactory> factoryManager = new FactoryManager<DummyLifeObject,UpdateFactory>(new RethrowingFactoryExceptionHandler<>());


        factoryManager.start(new RootFactoryWrapper<>(new UpdateFactory()));

        UpdateFactory common = factoryManager.getCurrentFactory().utility().copy();
        UpdateFactory update = factoryManager.getCurrentFactory().utility().copy();

        update.stringAttribute.set("123");

        Assertions.assertEquals(0,factoryManager.getCurrentFactory().updateCalls.size());
        update.resetCounter();
        factoryManager.update(common,update,(permission)->true);

        Assertions.assertEquals(1,factoryManager.getCurrentFactory().updateCalls.size());
    }

    @Test
    public void test_updater_start_not_called_again(){
        ch.qos.logback.classic.Logger rootLogger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        rootLogger.setLevel(Level.INFO);

        FactoryManager<DummyLifeObject,UpdateFactory> factoryManager = new FactoryManager<DummyLifeObject,UpdateFactory>(new RethrowingFactoryExceptionHandler<>());


        factoryManager.start(new RootFactoryWrapper<>(new UpdateFactory()));

        UpdateFactory common = factoryManager.getCurrentFactory().utility().copy();
        UpdateFactory update = factoryManager.getCurrentFactory().utility().copy();

        update.stringAttribute.set("123");

        Assertions.assertEquals(1,factoryManager.getCurrentFactory().startCalls.size());
        update.resetCounter();
        factoryManager.update(common,update,(permission)->true);

        Assertions.assertEquals(1,factoryManager.getCurrentFactory().startCalls.size());
    }

    @Test
    public void test_updater_destroy_not_called_again(){
        ch.qos.logback.classic.Logger rootLogger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        rootLogger.setLevel(Level.INFO);

        FactoryManager<DummyLifeObject,UpdateFactory> factoryManager = new FactoryManager<DummyLifeObject,UpdateFactory>(new RethrowingFactoryExceptionHandler<>());


        factoryManager.start(new RootFactoryWrapper<>(new UpdateFactory()));

        UpdateFactory common = factoryManager.getCurrentFactory().utility().copy();
        UpdateFactory update = factoryManager.getCurrentFactory().utility().copy();

        update.stringAttribute.set("123");

        Assertions.assertEquals(0,factoryManager.getCurrentFactory().destroyCalls.size());
        update.resetCounter();
        factoryManager.update(common,update,(permission)->true);

        Assertions.assertEquals(0,factoryManager.getCurrentFactory().destroyCalls.size());
    }

    @Test
    public void test_getLiveObjectCLass(){
        Assertions.assertEquals(DummyLifeObject.class, FactoryMetadataManager.getMetadata(LifecycleFactoryA.class).getLiveObjectClass());
    }
}