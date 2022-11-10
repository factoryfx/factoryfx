package io.github.factoryfx.server;

import com.google.common.base.Throwables;
import io.github.factoryfx.factory.attribute.types.StringAttribute;
import io.github.factoryfx.factory.attribute.types.StringListAttribute;
import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;
import io.github.factoryfx.factory.merge.AttributeDiffInfo;
import io.github.factoryfx.factory.merge.MergeDiffInfo;
import io.github.factoryfx.factory.storage.DataUpdate;
import io.github.factoryfx.factory.storage.StoredDataMetadata;
import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;
import io.github.factoryfx.factory.builder.FactoryTreeBuilder;
import io.github.factoryfx.factory.log.FactoryUpdateLog;
import io.github.factoryfx.factory.testfactories.ExampleFactoryA;
import io.github.factoryfx.factory.testfactories.ExampleFactoryB;
import io.github.factoryfx.factory.testfactories.ExampleLiveObjectA;
import io.github.factoryfx.factory.testfactories.ExampleLiveObjectB;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class MicroserviceTest {

    private static class ChangeListingSummary{
        public final List<UUID> changedIds;

        public ChangeListingSummary(List<UUID> changedIds) {
            this.changedIds = changedIds;
        }
    }

    @Test
    public void test_summary() throws Exception {
        FactoryTreeBuilder<ExampleLiveObjectA,ExampleFactoryA> builder = new FactoryTreeBuilder<>(ExampleFactoryA.class, ctx -> new ExampleFactoryA());

        Microservice<ExampleLiveObjectA,ExampleFactoryA> microservice = builder.microservice().build();
        microservice.start();

        UUID rootId=microservice.prepareNewFactory().root.getId();

        Thread.sleep(2);//avoid same timestamp
        {
            final DataUpdate<ExampleFactoryA> prepareNewFactory = microservice.prepareNewFactory();
            prepareNewFactory.root.stringAttribute.set("change1");
            microservice.updateCurrentFactory(prepareNewFactory);
        }
        Thread.sleep(2);//avoid same timestamp
        {
            final DataUpdate<ExampleFactoryA> prepareNewFactory = microservice.prepareNewFactory();
            prepareNewFactory.root.stringAttribute.set("change2");
            microservice.updateCurrentFactory(prepareNewFactory);
        }
        Thread.sleep(2);//avoid same timestamp
        {
            final DataUpdate<ExampleFactoryA> prepareNewFactory = microservice.prepareNewFactory();
            prepareNewFactory.root.stringAttribute.set("change3");
            microservice.updateCurrentFactory(prepareNewFactory);
        }
        Thread.sleep(2);//avoid same timestamp



        final List<StoredDataMetadata> historyFactoryList = new ArrayList<>(microservice.getHistoryFactoryList());
        Collections.sort(historyFactoryList, (o1, o2) -> Objects.compare(o1.creationTime, o2.creationTime, Comparator.reverseOrder()));



        Assertions.assertEquals(4,historyFactoryList.size());
        Assertions.assertEquals(1,historyFactoryList.get(2).changeSummary.changed.size());
        Assertions.assertEquals(rootId,historyFactoryList.get(2).changeSummary.changed.get(0).dataId);
        Assertions.assertEquals(1,historyFactoryList.get(1).changeSummary.changed.size());
        Assertions.assertEquals(rootId,historyFactoryList.get(1).changeSummary.changed.get(0).dataId);
        Assertions.assertEquals(1,historyFactoryList.get(2).changeSummary.changed.size());
        Assertions.assertEquals(rootId,historyFactoryList.get(0).changeSummary.changed.get(0).dataId);
    }

    @Test
    public void testUpdateReferenceList() {
        ExampleFactoryA root = new ExampleFactoryA();
        root.referenceListAttribute.add(new ExampleFactoryB());
        root =root.internal().finalise();

        FactoryTreeBuilder<ExampleLiveObjectA,ExampleFactoryA> builder = new FactoryTreeBuilder<>(ExampleFactoryA.class, ctx -> {
            return new ExampleFactoryA();
        });

        Microservice<ExampleLiveObjectA,ExampleFactoryA> microservice = builder.microservice().build();

        microservice.start();
        DataUpdate<ExampleFactoryA> editableFactory = microservice.prepareNewFactory();
        editableFactory.root.referenceListAttribute.add(new ExampleFactoryB());

        FactoryUpdateLog<ExampleFactoryA> log = microservice.updateCurrentFactory(editableFactory);
        AttributeDiffInfo theDiff = log.mergeDiffInfo.mergeInfos.get(0);

        String dt = theDiff.getAttributeDisplayText(log.mergeDiffInfo.getPreviousRootData());
        String dtNew = theDiff.getAttributeDisplayText(log.mergeDiffInfo.getNewRootData());
        Assertions.assertNotEquals(dt,dtNew);

    }

    @Test
    public void test_history() throws Exception {
        FactoryTreeBuilder<ExampleLiveObjectA,ExampleFactoryA> builder = new FactoryTreeBuilder<>(ExampleFactoryA.class, ctx -> new ExampleFactoryA());
        Microservice<ExampleLiveObjectA,ExampleFactoryA> microservice = builder.microservice().build();
        microservice.start();

        Thread.sleep(2);//avoid same timestamp
        {
            final DataUpdate<ExampleFactoryA> prepareNewFactory = microservice.prepareNewFactory();
            prepareNewFactory.root.stringAttribute.set("change1");
            microservice.updateCurrentFactory(prepareNewFactory);
        }
        Thread.sleep(2);//avoid same timestamp
        {
            final DataUpdate<ExampleFactoryA> prepareNewFactory = microservice.prepareNewFactory();
            prepareNewFactory.root.stringAttribute.set("change2");
            microservice.updateCurrentFactory(prepareNewFactory);
        }
        Thread.sleep(2);//avoid same timestamp
        {
            final DataUpdate<ExampleFactoryA> prepareNewFactory = microservice.prepareNewFactory();
            prepareNewFactory.root.stringAttribute.set("change3");
            microservice.updateCurrentFactory(prepareNewFactory);
        }
        Thread.sleep(2);//avoid same timestamp



        final List<StoredDataMetadata> historyFactoryList = new ArrayList<>(microservice.getHistoryFactoryList());
        Collections.sort(historyFactoryList, (o1, o2) -> Objects.compare(o1.creationTime, o2.creationTime, Comparator.reverseOrder()));

        Assertions.assertEquals(4,historyFactoryList.size());
        Assertions.assertEquals("change1", microservice.getHistoryFactory(historyFactoryList.get(2).id).stringAttribute.get());
        Assertions.assertEquals("change2", microservice.getHistoryFactory(historyFactoryList.get(1).id).stringAttribute.get());
        Assertions.assertEquals("change3", microservice.getHistoryFactory(historyFactoryList.get(0).id).stringAttribute.get());
    }


    public static class ExampleFactoryARecreation extends SimpleFactoryBase<ExampleLiveObjectA,ExampleFactoryARecreation> {
        public final FactoryAttribute<ExampleLiveObjectB,ExampleFactoryBRecreation> referenceAttribute = new FactoryAttribute<ExampleLiveObjectB,ExampleFactoryBRecreation>().labelText("ExampleA2").nullable();

        @Override
        protected ExampleLiveObjectA createImpl() {
            return null;
        }
    }

    public static class ExampleFactoryBRecreation extends FactoryBase<ExampleLiveObjectB,ExampleFactoryARecreation> {
        public final FactoryAttribute<ExampleLiveObjectB,ExampleFactoryBRecreation> referenceAttribute = new FactoryAttribute<ExampleLiveObjectB,ExampleFactoryBRecreation>().labelText("ExampleA2").nullable();

        long recreationCounter=0;
        public ExampleFactoryBRecreation(){
            this.configLifeCycle().setReCreator(exampleLiveObjectB -> {
                recreationCounter++;
                return null;
            });
            this.configLifeCycle().setCreator(() -> null);
        }
    }

    @Test
    public void recreation_bug() {
        FactoryTreeBuilder<ExampleLiveObjectA,ExampleFactoryARecreation> builder = new FactoryTreeBuilder<>(ExampleFactoryARecreation.class, ctx -> new ExampleFactoryARecreation());
        Microservice<ExampleLiveObjectA,ExampleFactoryARecreation> microservice = builder.microservice().build();

        microservice.start();

        DataUpdate<ExampleFactoryARecreation> update = microservice.prepareNewFactory();

        update.root.referenceAttribute.set(new ExampleFactoryBRecreation());

        Assertions.assertEquals(0,update.root.referenceAttribute.get().recreationCounter);
        microservice.updateCurrentFactory(update);
        Assertions.assertEquals(0,update.root.referenceAttribute.get().recreationCounter);
    }


    public static class BrokenFactory extends FactoryBase<Void,BrokenFactory> {
        public BrokenFactory(){
            configLifeCycle().setCreator(() -> {
                throw new RuntimeException("create");
            });
        }
    }

    @Test
    public void test_create_with_exception() {
        Assertions.assertThrows(RuntimeException.class, () -> {
            FactoryTreeBuilder< Void, BrokenFactory> builder = new FactoryTreeBuilder<>(BrokenFactory.class, ctx -> new BrokenFactory());
            Microservice<Void, BrokenFactory> microservice = builder.microservice().build();
            microservice.start();
        });
    }


    @Test
    public void test_prepareNewFactory_is_copy() {
        FactoryTreeBuilder<ExampleLiveObjectA,ExampleFactoryA> builder = new FactoryTreeBuilder<>(ExampleFactoryA.class, ctx -> new ExampleFactoryA());
        Microservice<ExampleLiveObjectA,ExampleFactoryA> microservice = builder.microservice().build();
        microservice.start();

        Assertions.assertFalse(microservice.prepareNewFactory().root==microservice.prepareNewFactory().root);
        Assertions.assertFalse(microservice.prepareNewFactory().root.referenceListAttribute==microservice.prepareNewFactory().root.referenceListAttribute);
    }

    @Test
    public void test_getDiffToPreviousVersion() {
        FactoryTreeBuilder<ExampleLiveObjectA,ExampleFactoryA> builder = new FactoryTreeBuilder<>(ExampleFactoryA.class, ctx -> new ExampleFactoryA());
        Microservice<ExampleLiveObjectA,ExampleFactoryA> microservice = builder.microservice().build();

        microservice.start();
        Assertions.assertEquals(0,microservice.prepareNewFactory().root.referenceListAttribute.size());
        Assertions.assertEquals(0,microservice.getHistoryFactory(new ArrayList<>(microservice.getHistoryFactoryList()).get(0).id).referenceListAttribute.size());

        DataUpdate<ExampleFactoryA> update = microservice.prepareNewFactory();
        update.comment="XXXX for sort";
        update.root.referenceListAttribute.add(new ExampleFactoryB());
        FactoryUpdateLog<ExampleFactoryA> log = microservice.updateCurrentFactory(update);

        Assertions.assertEquals(1,log.mergeDiffInfo.mergeInfos.size());
//        Assertions.assertEquals(1,microservice.prepareNewFactory().root.referenceListAttribute.size());

        Assertions.assertEquals(2,microservice.getHistoryFactoryList().size());
        List<StoredDataMetadata> historyFactoryList = new ArrayList<>(microservice.getHistoryFactoryList());
        historyFactoryList.sort(Comparator.comparing(o -> o.comment));
        Collections.reverse(historyFactoryList);

        Assertions.assertEquals(0,microservice.getHistoryFactory(historyFactoryList.get(0).id).referenceListAttribute.size());
        Assertions.assertEquals(1,microservice.getHistoryFactory(historyFactoryList.get(1).id).referenceListAttribute.size());
        MergeDiffInfo<ExampleFactoryA> diffToPreviousVersion = microservice.getDiffToPreviousVersion(historyFactoryList.get(1));

        Assertions.assertEquals(1,diffToPreviousVersion.mergeInfos.size());


    }

    @Test
    public void testUpdateReferenceList_parallel() {

        FactoryTreeBuilder<ExampleLiveObjectA,ExampleFactoryA> builder = new FactoryTreeBuilder<>(ExampleFactoryA.class, ctx -> new ExampleFactoryA());
        Microservice<ExampleLiveObjectA,ExampleFactoryA> microservice = builder.microservice().build();
        microservice.start();

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                Assertions.fail(e);
            }
        });

        List<Thread> threads = new ArrayList<>();
        List<Throwable> exceptions = Collections.synchronizedList(new ArrayList<>());
        AtomicInteger threadCount = new AtomicInteger(100);
        for (int i = 0; i < threadCount.get(); i++) {
            Thread thread = new Thread(() -> {
                try {
                    DataUpdate<ExampleFactoryA> update = microservice.prepareNewFactory();
                    update.root.referenceListAttribute.add(new ExampleFactoryB());
                    FactoryUpdateLog<ExampleFactoryA> exampleFactoryAFactoryUpdateLog = microservice.updateCurrentFactory(update);
                    threadCount.decrementAndGet();
//                    System.out.println(exampleFactoryAFactoryUpdateLog.successfullyMerged());
                } catch (Throwable e){
                    exceptions.add(e);
                }
            });
            threads.add(thread);
        }
        threads.forEach(Thread::start);
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        Assertions.assertEquals(0,exceptions.size(), exceptions.size()>0?Throwables.getStackTraceAsString(exceptions.get(0)):"no exception");
        Assertions.assertEquals(0,threadCount.get());


//        Assertions.assertEquals(10,microservice.prepareNewFactory().root.referenceListAttribute.size());

    }


    @Test
    public void test_list_override_recreation() {
        FactoryTreeBuilder<ExampleLiveObjectA,ExampleFactoryA> builder = new FactoryTreeBuilder<>(ExampleFactoryA.class, ctx -> new ExampleFactoryA());
        Microservice<ExampleLiveObjectA,ExampleFactoryA> microservice = builder.microservice().build();
        microservice.start();

        DataUpdate<ExampleFactoryA> updateFirst = microservice.prepareNewFactory();
        DataUpdate<ExampleFactoryA> updateSecond = microservice.prepareNewFactory();

        {
            ExampleFactoryB value = new ExampleFactoryB();
            value.stringAttribute.set("first update");
            updateFirst.root.referenceListAttribute.add(value);
        }
        microservice.updateCurrentFactory(updateFirst);

        Assertions.assertEquals("first update",microservice.prepareNewFactory().root.referenceListAttribute.get(0).stringAttribute.get());

        {
            ExampleFactoryB value = new ExampleFactoryB();
            value.stringAttribute.set("second update");
            updateSecond.root.referenceListAttribute.add(value);
        }
        FactoryUpdateLog<ExampleFactoryA> exampleFactoryAFactoryUpdateLog = microservice.updateCurrentFactory(updateSecond);

        Assertions.assertEquals(1,exampleFactoryAFactoryUpdateLog.mergeDiffInfo.conflictInfos.size());
        Assertions.assertEquals("first update",microservice.prepareNewFactory().root.referenceListAttribute.get(0).stringAttribute.get());

    }



    public static class ExampleFactoryUpdateA extends SimpleFactoryBase<ExampleUpdateA, ExampleFactoryUpdateA> {
        public final StringAttribute test= new StringAttribute().nullable();

        @Override
        protected ExampleUpdateA createImpl() {
            return new ExampleUpdateA(test.get(),this.internal().copy(),this.utility().getMicroservice());
        }
    }

    public static class ExampleUpdateA  {
        private final ExampleFactoryUpdateA copyForUpdate;
        private final String test;
        private final Microservice<?,ExampleFactoryUpdateA> microservice;
        public ExampleUpdateA(String test, ExampleFactoryUpdateA copy, Microservice<?,ExampleFactoryUpdateA> microservice ) {
            copyForUpdate=copy;
            this.test=test;
            this.microservice=microservice;
        }

        public void update(){
            copyForUpdate.test.set("123");
            DataUpdate<ExampleFactoryUpdateA> update = microservice.prepareNewFactory();
            update.root=copyForUpdate;
            microservice.updateCurrentFactory(update);
        }

        public String print() {
            return test;
        }
    }

    @Test
    public void test_selfUpdate(){
        FactoryTreeBuilder<ExampleUpdateA,ExampleFactoryUpdateA> builder = new FactoryTreeBuilder<>(ExampleFactoryUpdateA.class, ctx -> new ExampleFactoryUpdateA());
        Microservice<ExampleUpdateA,ExampleFactoryUpdateA> microservice = builder.microservice().build();
        ExampleUpdateA exampleUpdateA = microservice.start();
        Assertions.assertNotEquals("123",microservice.getRootLiveObject().print());
        microservice.getRootLiveObject().update();
        Assertions.assertEquals("123",microservice.getRootLiveObject().print());

    }


    @Test
    public void test_update(){
        FactoryTreeBuilder<ExampleLiveObjectA,ExampleFactoryA> builder = new FactoryTreeBuilder<>(ExampleFactoryA.class, ctx -> new ExampleFactoryA());

        Microservice<ExampleLiveObjectA,ExampleFactoryA> microservice = builder.microservice().build();
        microservice.start();
        Assertions.assertEquals(1,microservice.getHistoryFactoryList().size());
        microservice.update((root, idToFactory) -> root.referenceAttribute.set(new ExampleFactoryB()));

        Assertions.assertEquals(2,microservice.getHistoryFactoryList().size());
    }

    public static class ExampleFactoryBDestroyTracking extends ExampleFactoryB{
        static int destroyCalled=0;
        public ExampleFactoryBDestroyTracking(){
            this.configLifeCycle().setDestroyer(exampleLiveObjectB -> destroyCalled++);
        }
    }

    @Test
    public void test_removed_factories() {
        FactoryTreeBuilder<ExampleLiveObjectA,ExampleFactoryA> builder = new FactoryTreeBuilder<>(ExampleFactoryA.class, ctx -> new ExampleFactoryA());
        Microservice<ExampleLiveObjectA,ExampleFactoryA> microservice = builder.microservice().build();

        microservice.start();

        {
            DataUpdate<ExampleFactoryA> update = microservice.prepareNewFactory();
            update.root.referenceAttribute.set(new ExampleFactoryBDestroyTracking());
            microservice.updateCurrentFactory(update);
        }


        {
            DataUpdate<ExampleFactoryA> update = microservice.prepareNewFactory();
            update.root.referenceListAttribute.add(new ExampleFactoryBDestroyTracking());
            microservice.updateCurrentFactory(update);
        }
        Assertions.assertEquals(0,ExampleFactoryBDestroyTracking.destroyCalled);

        {
            DataUpdate<ExampleFactoryA> update = microservice.prepareNewFactory();
            update.root.referenceAttribute.set(null);
            microservice.updateCurrentFactory(update);
        }
        Assertions.assertEquals(1,ExampleFactoryBDestroyTracking.destroyCalled);

    }

    public static record LiveObjectWithSet(Set<String> stringSet){
    }
    public static class ExampleFactoryAWithList extends SimpleFactoryBase<LiveObjectWithSet,ExampleFactoryAWithList> {
        public final StringListAttribute list= new StringListAttribute();

        @Override
        protected LiveObjectWithSet createImpl() {
            return new LiveObjectWithSet(new HashSet<>(list.get()));
        }
    }

    @Test
    public void testUpdateValueList() {
        FactoryTreeBuilder<LiveObjectWithSet,ExampleFactoryAWithList> builder = new FactoryTreeBuilder<>(ExampleFactoryAWithList.class, ctx -> {
            return new ExampleFactoryAWithList();
        });

        Microservice<LiveObjectWithSet,ExampleFactoryAWithList> microservice = builder.microservice().build();

        microservice.start();

        {//add
            DataUpdate<ExampleFactoryAWithList> editableFactory = microservice.prepareNewFactory();
            editableFactory = ObjectMapperBuilder.build().copy(editableFactory);//simulate network
            editableFactory.root.list.add("222222");
            FactoryUpdateLog<ExampleFactoryAWithList> log = microservice.updateCurrentFactory(editableFactory);
            Assertions.assertEquals(1, log.mergeDiffInfo.mergeInfos.size());
            Assertions.assertEquals(Set.of("222222"), microservice.getRootLiveObject().stringSet);
        }


        {//remove
            DataUpdate<ExampleFactoryAWithList> editableFactory = microservice.prepareNewFactory();
            editableFactory = ObjectMapperBuilder.build().copy(editableFactory);//simulate network
            editableFactory.root.list.remove(0);
            FactoryUpdateLog<ExampleFactoryAWithList> log = microservice.updateCurrentFactory(editableFactory);
            Assertions.assertEquals(1, log.mergeDiffInfo.mergeInfos.size());
            Assertions.assertEquals(Set.of(), microservice.getRootLiveObject().stringSet);
        }

    }
}