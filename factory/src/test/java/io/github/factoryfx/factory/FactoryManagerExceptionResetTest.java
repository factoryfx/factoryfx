package io.github.factoryfx.factory;

import io.github.factoryfx.data.DataDictionary;
import io.github.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import io.github.factoryfx.factory.exception.ResettingHandler;
import io.github.factoryfx.factory.log.FactoryUpdateLog;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class FactoryManagerExceptionResetTest {

    public static class DummyLifeObejct {

    }


    public static class BrokenFactory extends FactoryBase<DummyLifeObejct,BrokenFactory> {
        public final FactoryReferenceAttribute<DummyLifeObejct,BrokenFactory> ref= new FactoryReferenceAttribute<>(BrokenFactory.class);

        public List<String> createCalls= new ArrayList<>();
        public List<String> reCreateCalls= new ArrayList<>();
        public List<String> startCalls= new ArrayList<>();
        public List<String> destroyCalls= new ArrayList<>();

        static {
            DataDictionary.getDataDictionary(BrokenFactory.class).setNewCopyInstanceSupplier(brokenFactory ->
                    new BrokenFactory(brokenFactory.createException,brokenFactory.reCreateException,brokenFactory.startException,brokenFactory.destroyException)
            );
        }

        private boolean createException;
        private boolean reCreateException;
        private boolean startException;
        private boolean destroyException;

        public BrokenFactory(boolean createException, boolean reCreateException, boolean startException, boolean destroyException){
            this.createException=createException;
            this.reCreateException=reCreateException;
            this.startException=startException;
            this.destroyException=destroyException;

            configLifeCycle().setCreator(() -> {
                createCalls.add("created");
                if (this.createException){
                    throw new RuntimeException("create");
                }
                return new DummyLifeObejct();
            });
            configLifeCycle().setReCreator(dummyLifeObejct -> {
                reCreateCalls.add("recreate");
                if (this.reCreateException){
                    throw new RuntimeException("recreate");
                }
                return new DummyLifeObejct();
            });
            configLifeCycle().setDestroyer(dummyLifeObejct -> {
                destroyCalls.add("destroy");
                if (this.destroyException){
                    throw new RuntimeException("destroy");
                }
            });
            configLifeCycle().setStarter(dummyLifeObejct -> {
                startCalls.add("start");
                if (this.startException){
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
            FactoryManager<DummyLifeObejct, BrokenFactory> factoryManager = new FactoryManager<>(new ResettingHandler());

            BrokenFactory root = new BrokenFactory(false, false, false, false);
            root.ref.set(new BrokenFactory(true, false, false, false));
            factoryManager.start(new RootFactoryWrapper<>(root));
        });
    }

    @Test
    public void test_exception_start_start(){
        Assertions.assertThrows(RuntimeException.class, () -> {
            FactoryManager< DummyLifeObejct, BrokenFactory> factoryManager = new FactoryManager<>(new ResettingHandler());

            BrokenFactory root = new BrokenFactory(false, false, false, false);
            root.ref.set(new BrokenFactory(false, false, true, false));
            factoryManager.start(new RootFactoryWrapper<>(root));
        });
    }

    @Test
    public void test_exception_update_create(){
        FactoryManager<DummyLifeObejct,BrokenFactory> factoryManager = new FactoryManager<>(new ResettingHandler());

        BrokenFactory root = new BrokenFactory(false, false, false,false);
        factoryManager.start(new RootFactoryWrapper<>(root));
        root.resetCounter();

        BrokenFactory update = factoryManager.getCurrentFactory().utility().copy();
        update.ref.set(new BrokenFactory(true,false,false,false));

        FactoryUpdateLog<BrokenFactory> updateLog = factoryManager.update(factoryManager.getCurrentFactory().utility().copy(), update, p -> true);

        Assertions.assertTrue(updateLog.failedUpdate());
        Assertions.assertEquals(1,root.destroyCalls.size());//restart with previous config before update

        Assertions.assertEquals(1,factoryManager.getCurrentFactory().createCalls.size());
        Assertions.assertEquals(1,factoryManager.getCurrentFactory().startCalls.size());

        Assertions.assertEquals(1,factoryManager.getCurrentFactory().internal().collectChildrenDeep().size());//the update is reverted
    }

    @Test
    public void test_exception_update_start(){
        FactoryManager<DummyLifeObejct,BrokenFactory> factoryManager = new FactoryManager<>(new ResettingHandler());

        BrokenFactory root = new BrokenFactory(false, false, false,false);
        factoryManager.start(new RootFactoryWrapper<>(root));
        root.resetCounter();

        BrokenFactory update = factoryManager.getCurrentFactory().utility().copy();
        update.ref.set(new BrokenFactory(false,false,true,false));

        factoryManager.update(factoryManager.getCurrentFactory().utility().copy(),update,p->true);

        Assertions.assertEquals(2,root.destroyCalls.size());//1 for the update and 1 for the reset
        Assertions.assertEquals(1,factoryManager.getCurrentFactory().createCalls.size());
        Assertions.assertEquals(1,factoryManager.getCurrentFactory().startCalls.size());

        Assertions.assertEquals(1,factoryManager.getCurrentFactory().internal().collectChildrenDeep().size());//the update is reverted
    }

    @Test
    public void test_exception_update_recreate(){
        FactoryManager<DummyLifeObejct,BrokenFactory> factoryManager = new FactoryManager<>(new ResettingHandler());

        BrokenFactory root = new BrokenFactory(false, true, false,false);
        factoryManager.start(new RootFactoryWrapper<>(root));
        root.resetCounter();

        BrokenFactory update = factoryManager.getCurrentFactory().utility().copy();
        update.ref.set(new BrokenFactory(false,false,false,false));

        factoryManager.update(factoryManager.getCurrentFactory().utility().copy(),update,p->true);

        Assertions.assertEquals(2,root.destroyCalls.size());//1 for the update and 1 for the reset
        Assertions.assertEquals(1,factoryManager.getCurrentFactory().createCalls.size());
        Assertions.assertEquals(1,factoryManager.getCurrentFactory().startCalls.size());

        Assertions.assertEquals(1,factoryManager.getCurrentFactory().internal().collectChildrenDeep().size());//the update is reverted
    }

    @Test
    public void test_exception_destroy(){
        Assertions.assertThrows(RuntimeException.class, () -> {
            FactoryManager< DummyLifeObejct, BrokenFactory> factoryManager = new FactoryManager<>(new ResettingHandler());

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
    public void test_exception_update_noloop(){
        Assertions.assertThrows(RuntimeException.class, () -> {
            FactoryManager< DummyLifeObejct, BrokenFactory> factoryManager = new FactoryManager<>(new ResettingHandler());

            BrokenFactory root = new BrokenFactory(false, false, false, false);
            factoryManager.start(new RootFactoryWrapper<>(root));
            root.resetCounter();

            BrokenFactory update = factoryManager.getCurrentFactory().utility().copy();
            update.ref.set(new BrokenFactory(false, false, true, false));

            root.createException = true;//reset should also fail

            FactoryUpdateLog<BrokenFactory> updateLog = factoryManager.update(factoryManager.getCurrentFactory().utility().copy(), update, p -> true);
            Assertions.assertTrue(updateLog.failedUpdate());
        });
    }

    @Test
    public void test_exception_update___destroy_for_removed(){
        FactoryManager<DummyLifeObejct,BrokenFactory> factoryManager = new FactoryManager<>(new ResettingHandler());

        BrokenFactory root = new BrokenFactory(false, false, false,false);
        root.ref.set(new BrokenFactory(false,false,false,false));

        factoryManager.start(new RootFactoryWrapper<>(root));
        root.resetCounter();

        BrokenFactory update = factoryManager.getCurrentFactory().utility().copy();
        BrokenFactory removedFactory = root.ref.get();
        update.ref.set(null);
        root.reCreateException=true;
        FactoryUpdateLog<BrokenFactory> updateLog = factoryManager.update(factoryManager.getCurrentFactory().utility().copy(), update, p -> true);

        Assertions.assertTrue(updateLog.failedUpdate());
        Assertions.assertEquals(1,removedFactory.destroyCalls.size());//restart with previous config before update
    }
}