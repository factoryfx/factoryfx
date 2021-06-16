package io.github.factoryfx.factory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import io.github.factoryfx.factory.attribute.primitive.BooleanAttribute;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;
import io.github.factoryfx.factory.exception.ResettingHandler;
import io.github.factoryfx.factory.log.FactoryUpdateLog;
import io.github.factoryfx.factory.metadata.FactoryMetadataManager;

public class FactoryManagerExceptionResetTest {

    public static class DummyLifeObejct {

    }


    public static class BrokenFactory extends FactoryBase<DummyLifeObejct,BrokenFactory> {
        public final FactoryAttribute<DummyLifeObejct,BrokenFactory> ref= new FactoryAttribute<>();

        public List<String> createCalls= new ArrayList<>();
        public List<String> reCreateCalls= new ArrayList<>();
        public List<String> startCalls= new ArrayList<>();
        public List<String> destroyCalls= new ArrayList<>();

        static {
            FactoryMetadataManager.getMetadata(BrokenFactory.class).setNewCopyInstanceSupplier(brokenFactory ->
                    new BrokenFactory(brokenFactory.createException.get(),brokenFactory.reCreateException.get(),brokenFactory.startException.get(),brokenFactory.destroyException.get())
            );
        }

        public final BooleanAttribute createException=new BooleanAttribute();
        public final BooleanAttribute reCreateException=new BooleanAttribute();
        public final BooleanAttribute startException=new BooleanAttribute();
        public final BooleanAttribute destroyException=new BooleanAttribute();

        public BrokenFactory() {
             this(false,false,false,false);
        }

        public BrokenFactory(boolean createException, boolean reCreateException, boolean startException, boolean destroyException){
            this.createException.set(createException);
            this.reCreateException.set(reCreateException);
            this.startException.set(startException);
            this.destroyException.set(destroyException);

            configLifeCycle().setCreator(() -> {
                createCalls.add("created");
                if (this.createException.get()){
                    throw new RuntimeException("create");
                }
                return new DummyLifeObejct();
            });
            configLifeCycle().setReCreator(dummyLifeObject -> {
                reCreateCalls.add("recreate");
                if (this.reCreateException.get()){
                    throw new RuntimeException("recreate");
                }
                return new DummyLifeObejct();
            });
            configLifeCycle().setDestroyer(dummyLifeObject -> {
                destroyCalls.add("destroy");
                if (this.destroyException.get()){
                    throw new RuntimeException("destroy");
                }
            });
            configLifeCycle().setStarter(dummyLifeObejct -> {
                startCalls.add("start");
                if (this.startException.get()){
                    throw new RuntimeException("start");
                }
            });
        }

        public void resetCounter(){
            createCalls.clear();
            reCreateCalls.clear();
            startCalls.clear();
            destroyCalls.clear();
        }
    }

    @Test
    public void test_exception_start_create() {
        Assertions.assertThrows(RuntimeException.class, () -> {
            FactoryManager<DummyLifeObejct, BrokenFactory> factoryManager = new FactoryManager<DummyLifeObejct, BrokenFactory>(new ResettingHandler<>());

            BrokenFactory root = new BrokenFactory(false, false, false, false);
            root.ref.set(new BrokenFactory(true, false, false, false));
            factoryManager.start(new RootFactoryWrapper<>(root));
        });
    }

    @Test
    public void test_exception_start_start(){
        Assertions.assertThrows(RuntimeException.class, () -> {
            FactoryManager< DummyLifeObejct, BrokenFactory> factoryManager = new FactoryManager<DummyLifeObejct, BrokenFactory>(new ResettingHandler<>());

            BrokenFactory root = new BrokenFactory(false, false, false, false);
            root.ref.set(new BrokenFactory(false, false, true, false));
            factoryManager.start(new RootFactoryWrapper<>(root));
        });
    }

    @Test
    public void test_exception_update_create(){
        FactoryManager<DummyLifeObejct,BrokenFactory> factoryManager = new FactoryManager<DummyLifeObejct, BrokenFactory>(new ResettingHandler<>());

        BrokenFactory root = new BrokenFactory(false, false, false,false);
        factoryManager.start(new RootFactoryWrapper<>(root));
        root.resetCounter();

        FactoryUpdateLog<BrokenFactory> updateLog = factoryManager.update((root1, idToFactory) -> {
            root1.ref.set(new BrokenFactory(true, false, false, false));
        });

        Assertions.assertTrue(updateLog.failedUpdate());
        Assertions.assertEquals(1,root.destroyCalls.size());//restart with previous config before update

        Assertions.assertEquals(1,factoryManager.getCurrentFactory().createCalls.size());
        Assertions.assertEquals(1,factoryManager.getCurrentFactory().startCalls.size());

        Assertions.assertEquals(1,factoryManager.getCurrentFactory().internal().collectChildrenDeep().size());//the update is reverted
    }

    @Test
    public void test_exception_update_start(){
        FactoryManager<DummyLifeObejct,BrokenFactory> factoryManager = new FactoryManager<DummyLifeObejct, BrokenFactory>(new ResettingHandler<>());

        BrokenFactory root = new BrokenFactory(false, false, false,false);
        factoryManager.start(new RootFactoryWrapper<>(root));
        root.resetCounter();

        BrokenFactory update = factoryManager.getCurrentFactory().utility().copy();
        update.ref.set(new BrokenFactory(false,false,true,false));

        FactoryUpdateLog<BrokenFactory> updateLog = factoryManager.update(factoryManager.getCurrentFactory().utility().copy(), update, p -> true);

        Assertions.assertTrue(updateLog.failedUpdate());
        Assertions.assertEquals(2,root.destroyCalls.size());//1 for the update and 1 for the reset
        Assertions.assertEquals(1,factoryManager.getCurrentFactory().createCalls.size());
        Assertions.assertEquals(1,factoryManager.getCurrentFactory().startCalls.size());

        Assertions.assertEquals(1,factoryManager.getCurrentFactory().internal().collectChildrenDeep().size());//the update is reverted
    }

    @Test
    public void test_exception_update_recreate(){
        FactoryManager<DummyLifeObejct,BrokenFactory> factoryManager = new FactoryManager<DummyLifeObejct, BrokenFactory>(new ResettingHandler<>());

        BrokenFactory root = new BrokenFactory(false, false, false,false);
        factoryManager.start(new RootFactoryWrapper<>(root));
        root.resetCounter();

        BrokenFactory update = factoryManager.getCurrentFactory().utility().copy();
        update.ref.set(new BrokenFactory(false,false,false,false));
        update.reCreateException.set(true);

        FactoryUpdateLog<BrokenFactory> updateLog = factoryManager.update(factoryManager.getCurrentFactory().utility().copy(),update, p->true);

        Assertions.assertTrue(updateLog.failedUpdate());
        Assertions.assertEquals(2,root.destroyCalls.size());//1 for the update and 1 for the reset
        Assertions.assertEquals(1,factoryManager.getCurrentFactory().createCalls.size());
        Assertions.assertEquals(1,factoryManager.getCurrentFactory().startCalls.size());

        Assertions.assertEquals(1,factoryManager.getCurrentFactory().internal().collectChildrenDeep().size());//the update is reverted
    }

    @Test
    public void test_exception_destroy(){
        Assertions.assertThrows(RuntimeException.class, () -> {
            FactoryManager< DummyLifeObejct, BrokenFactory> factoryManager = new FactoryManager<DummyLifeObejct, BrokenFactory>(new ResettingHandler<>());

            BrokenFactory root = new BrokenFactory(false, false, false, true);
            factoryManager.start(new RootFactoryWrapper<>(root));
            root.resetCounter();
            factoryManager.stop();


            Assertions.assertEquals(2, root.destroyCalls.size());//1 for the stop and 1 for the reset

//        Assertions.assertEquals(1,factoryManager.getCurrentData().createCalls.size());
//        Assertions.assertEquals(1,factoryManager.getCurrentData().startCalls.size());
//        Assertions.assertEquals(1,factoryManager.getCurrentData().internal().collectChildrenDeep().size());//the update is reverted
        });
    }


    @Test
    public void test_start_exception(){
        FactoryManager< DummyLifeObejct, BrokenFactory> factoryManager = new FactoryManager<DummyLifeObejct, BrokenFactory>(new ResettingHandler<>());

        BrokenFactory root = new BrokenFactory(false, false, false, false);
        factoryManager.start(new RootFactoryWrapper<>(root));
        root.resetCounter();

        BrokenFactory update = factoryManager.getCurrentFactory().utility().copy();
        update.ref.set(new BrokenFactory(false, false, true, false));


        FactoryUpdateLog<BrokenFactory> updateLog = factoryManager.update(factoryManager.getCurrentFactory().utility().copy(), update, p -> true);
        Assertions.assertTrue(updateLog.failedUpdate());
        updateLog.dumpError(System.out::println);
    }

    @Test
    public void test_exception_update___destroy_for_removed(){
        FactoryManager<DummyLifeObejct,BrokenFactory> factoryManager = new FactoryManager<DummyLifeObejct, BrokenFactory>(new ResettingHandler<>());

        BrokenFactory root = new BrokenFactory(false, false, false,false);
        root.ref.set(new BrokenFactory(false,false,false,false));

        factoryManager.start(new RootFactoryWrapper<>(root));
        root.resetCounter();

        BrokenFactory update = factoryManager.getCurrentFactory().utility().copy();
        BrokenFactory removedFactory = root.ref.get();
        update.ref.set(null);
        FactoryUpdateLog<BrokenFactory> updateLog = factoryManager.update(factoryManager.getCurrentFactory().utility().copy(), update, p -> true);
        updateLog.dumpError(System.out::println);
        Assertions.assertEquals(1,removedFactory.destroyCalls.size());//restart with previous config before update
    }
}