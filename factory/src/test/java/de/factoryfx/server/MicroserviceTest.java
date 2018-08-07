package de.factoryfx.server;

import de.factoryfx.data.merge.AttributeDiffInfo;
import de.factoryfx.data.merge.MergeDiffInfo;
import de.factoryfx.data.storage.ChangeSummaryCreator;
import de.factoryfx.data.storage.DataAndNewMetadata;
import de.factoryfx.data.storage.StoredDataMetadata;
import de.factoryfx.data.storage.inmemory.InMemoryDataStorage;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.FactoryManager;
import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.factory.exception.ResettingHandler;
import de.factoryfx.factory.exception.RethrowingFactoryExceptionHandler;
import de.factoryfx.factory.log.FactoryUpdateLog;
import de.factoryfx.factory.testfactories.ExampleFactoryA;
import de.factoryfx.factory.testfactories.ExampleFactoryB;
import de.factoryfx.factory.testfactories.ExampleLiveObjectA;
import de.factoryfx.factory.testfactories.ExampleLiveObjectB;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;

public class MicroserviceTest {

    private static class ChangeListingSummary{
        public final List<String> changedIds;

        public ChangeListingSummary(List<String> changedIds) {
            this.changedIds = changedIds;
        }
    }

    @Test
    public void test_summary() throws Exception {
        ExampleFactoryA root = new ExampleFactoryA();

        String rootId=root.getId();
        final InMemoryDataStorage<ExampleFactoryA, ChangeListingSummary> memoryFactoryStorage = new InMemoryDataStorage<>(root, new ChangeSummaryCreator<ExampleFactoryA, ChangeListingSummary>() {
            @Override
            public ChangeListingSummary createChangeSummary(MergeDiffInfo<ExampleFactoryA> mergeDiffInfo) {
                if (mergeDiffInfo==null){
                    return null;
                }
                return new ChangeListingSummary(mergeDiffInfo.mergeInfos.stream().map((m)->m.dataId).collect(Collectors.toList()));
            }
        });

        Microservice<Void,ExampleLiveObjectA,ExampleFactoryA,ChangeListingSummary> microservice = new Microservice<>(new FactoryManager<>(new RethrowingFactoryExceptionHandler()), memoryFactoryStorage);
        microservice.start();

        Thread.sleep(2);//avoid same timestamp
        {
            final DataAndNewMetadata<ExampleFactoryA> prepareNewFactory = microservice.prepareNewFactory();
            prepareNewFactory.root.stringAttribute.set("change1");
            microservice.updateCurrentFactory(prepareNewFactory, "user", "comment1",(p)->true);
        }
        Thread.sleep(2);//avoid same timestamp
        {
            final DataAndNewMetadata<ExampleFactoryA> prepareNewFactory = microservice.prepareNewFactory();
            prepareNewFactory.root.stringAttribute.set("change2");
            microservice.updateCurrentFactory(prepareNewFactory, "user", "comment2",(p)->true);
        }
        Thread.sleep(2);//avoid same timestamp
        {
            final DataAndNewMetadata<ExampleFactoryA> prepareNewFactory = microservice.prepareNewFactory();
            prepareNewFactory.root.stringAttribute.set("change3");
            microservice.updateCurrentFactory(prepareNewFactory, "user", "comment3",(p)->true);
        }
        Thread.sleep(2);//avoid same timestamp



        final List<StoredDataMetadata<ChangeListingSummary>> historyFactoryList = new ArrayList<>(microservice.getHistoryFactoryList());
        Collections.sort(historyFactoryList, (o1, o2) -> Objects.compare(o1.creationTime, o2.creationTime, Comparator.reverseOrder()));



        Assert.assertEquals(4,historyFactoryList.size());
        Assert.assertEquals(1,historyFactoryList.get(2).changeSummary.changedIds.size());
        Assert.assertEquals(rootId,historyFactoryList.get(2).changeSummary.changedIds.get(0));
        Assert.assertEquals(1,historyFactoryList.get(1).changeSummary.changedIds.size());
        Assert.assertEquals(rootId,historyFactoryList.get(1).changeSummary.changedIds.get(0));
        Assert.assertEquals(1,historyFactoryList.get(2).changeSummary.changedIds.size());
        Assert.assertEquals(rootId,historyFactoryList.get(0).changeSummary.changedIds.get(0));
    }

    @Test
    public void testUpdateReferenceList() {
        ExampleFactoryA root = new ExampleFactoryA();
        root.referenceListAttribute.add(new ExampleFactoryB());
        root =root.internal().addBackReferences();
        final InMemoryDataStorage<ExampleFactoryA,Void> memoryFactoryStorage = new InMemoryDataStorage<>(root);
        memoryFactoryStorage.loadInitialFactory();

        Microservice<Void,ExampleLiveObjectA,ExampleFactoryA,Void> microservice = new Microservice<>(new FactoryManager<>(new RethrowingFactoryExceptionHandler()), memoryFactoryStorage);
        microservice.start();
        DataAndNewMetadata<ExampleFactoryA> editableFactory = microservice.prepareNewFactory();
        editableFactory.root.referenceListAttribute.add(new ExampleFactoryB());

        FactoryUpdateLog<ExampleFactoryA> log = microservice.updateCurrentFactory(editableFactory,"","", x->true);
        AttributeDiffInfo theDiff = log.mergeDiffInfo.mergeInfos.get(0);

        String dt = theDiff.getAttributeDisplayText(log.mergeDiffInfo.getPreviousRootData());
        String dtNew = theDiff.getAttributeDisplayText(log.mergeDiffInfo.getNewRootData());
        Assert.assertNotEquals(dt,dtNew);

    }

    @Test
    public void test_history() throws Exception {
        Microservice<Void,ExampleLiveObjectA,ExampleFactoryA,Void> microservice = MicroserviceBuilder.buildInMemoryMicroservice(new ExampleFactoryA());
        microservice.start();

        Thread.sleep(2);//avoid same timestamp
        {
            final DataAndNewMetadata<ExampleFactoryA> prepareNewFactory = microservice.prepareNewFactory();
            prepareNewFactory.root.stringAttribute.set("change1");
            microservice.updateCurrentFactory(prepareNewFactory, "user", "comment1",null);
        }
        Thread.sleep(2);//avoid same timestamp
        {
            final DataAndNewMetadata<ExampleFactoryA> prepareNewFactory = microservice.prepareNewFactory();
            prepareNewFactory.root.stringAttribute.set("change2");
            microservice.updateCurrentFactory(prepareNewFactory, "user", "comment2",null);
        }
        Thread.sleep(2);//avoid same timestamp
        {
            final DataAndNewMetadata<ExampleFactoryA> prepareNewFactory = microservice.prepareNewFactory();
            prepareNewFactory.root.stringAttribute.set("change3");
            microservice.updateCurrentFactory(prepareNewFactory, "user", "comment3",null);
        }
        Thread.sleep(2);//avoid same timestamp



        final List<StoredDataMetadata<Void>> historyFactoryList = new ArrayList<>(microservice.getHistoryFactoryList());
        Collections.sort(historyFactoryList, (o1, o2) -> Objects.compare(o1.creationTime, o2.creationTime, Comparator.reverseOrder()));

        Assert.assertEquals(4,historyFactoryList.size());
        Assert.assertEquals("change1", microservice.getHistoryFactory(historyFactoryList.get(2).id).stringAttribute.get());
        Assert.assertEquals("change2", microservice.getHistoryFactory(historyFactoryList.get(1).id).stringAttribute.get());
        Assert.assertEquals("change3", microservice.getHistoryFactory(historyFactoryList.get(0).id).stringAttribute.get());
    }


    public static class ExampleFactoryARecreation extends SimpleFactoryBase<ExampleLiveObjectA,Void,ExampleFactoryARecreation> {
        public final FactoryReferenceAttribute<ExampleLiveObjectB,ExampleFactoryBRecreation> referenceAttribute = new FactoryReferenceAttribute<>(ExampleFactoryBRecreation.class).labelText("ExampleA2");

        @Override
        public ExampleLiveObjectA createImpl() {
            return null;
        }
    }

    public static class ExampleFactoryBRecreation extends FactoryBase<ExampleLiveObjectB,Void,ExampleFactoryARecreation> {
        public final FactoryReferenceAttribute<ExampleLiveObjectB,ExampleFactoryB> referenceAttribute = new FactoryReferenceAttribute<>(ExampleFactoryB.class).labelText("ExampleA2");

        long recreationCounter=0;
        public ExampleFactoryBRecreation(){
            this.configLiveCycle().setReCreator(exampleLiveObjectB -> {
                recreationCounter++;
                return null;
            });
            this.configLiveCycle().setCreator(() -> null);
        }
    }
    @Test
    public void recreation_bug() {

        ExampleFactoryARecreation root = new ExampleFactoryARecreation();
        final InMemoryDataStorage<ExampleFactoryARecreation, Void> memoryFactoryStorage = new InMemoryDataStorage<>(root);
        Microservice<Void,ExampleLiveObjectA,ExampleFactoryARecreation,Void> microservice = new Microservice<>(new FactoryManager<>(new RethrowingFactoryExceptionHandler()), memoryFactoryStorage);

        microservice.start();

        DataAndNewMetadata<ExampleFactoryARecreation> update = microservice.prepareNewFactory();

        update.root.referenceAttribute.set(new ExampleFactoryBRecreation());

        Assert.assertEquals(0,update.root.referenceAttribute.get().recreationCounter);
        microservice.updateCurrentFactory(update, "", "", s -> true);
        Assert.assertEquals(0,update.root.referenceAttribute.get().recreationCounter);
    }


    public static class BrokenFactory extends FactoryBase<Void,Void,BrokenFactory> {
        public BrokenFactory(){
            configLiveCycle().setCreator(() -> {
                throw new RuntimeException("create");
            });
        }
    }

    @Test(expected = RuntimeException.class)
    public void test_create_width_exception() throws Exception {

        InMemoryDataStorage<BrokenFactory, ChangeListingSummary> memoryDataStorage = new InMemoryDataStorage<>(new BrokenFactory());
        Assert.assertEquals(0,memoryDataStorage.getFutureFactoryList().size());

        Microservice<Void,Void,BrokenFactory,ChangeListingSummary> microservice = new Microservice<>(new FactoryManager<>(new ResettingHandler()), memoryDataStorage);
        microservice.start();

//        microservice.updateCurrentFactory(microservice.prepareNewFactory(),"","",(p)->true);
//
//        Assert.assertEquals(0,memoryDataStorage.getFutureFactoryList().size());
    }


    @Test
    public void test_getDiffToPreviousVersion() {
        ExampleFactoryA root = new ExampleFactoryA();
        final InMemoryDataStorage<ExampleFactoryA,Void> memoryFactoryStorage = new InMemoryDataStorage<>(root);

        Microservice<Void,ExampleLiveObjectA,ExampleFactoryA,Void> microservice = new Microservice<>(new FactoryManager<>(new RethrowingFactoryExceptionHandler()), memoryFactoryStorage);
        microservice.start();
        DataAndNewMetadata<ExampleFactoryA> update = microservice.prepareNewFactory();
        update.root.referenceListAttribute.add(new ExampleFactoryB());

        Assert.assertEquals(0,microservice.prepareNewFactory().root.referenceListAttribute.size());

        FactoryUpdateLog<ExampleFactoryA> log = microservice.updateCurrentFactory(update,"","", x->true);
        Assert.assertEquals(1,log.mergeDiffInfo.mergeInfos.size());
        Assert.assertEquals(1,microservice.prepareNewFactory().root.referenceListAttribute.size());

        Assert.assertEquals(2,microservice.getHistoryFactoryList().size());
        List<StoredDataMetadata<Void>> historyFactoryList = new ArrayList<>(microservice.getHistoryFactoryList());
        historyFactoryList.sort(Comparator.comparing(o -> o.creationTime));
        MergeDiffInfo<ExampleFactoryA> diffToPreviousVersion = microservice.getDiffToPreviousVersion(historyFactoryList.get(1));

        Assert.assertEquals(1,diffToPreviousVersion.mergeInfos.size());


    }
}