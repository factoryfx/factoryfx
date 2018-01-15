package de.factoryfx.server;

import de.factoryfx.data.attribute.types.StringAttribute;
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
import de.factoryfx.factory.atrribute.FactoryReferenceListAttribute;
import de.factoryfx.factory.exception.AllOrNothingFactoryExceptionHandler;
import de.factoryfx.factory.exception.LoggingFactoryExceptionHandler;
import de.factoryfx.factory.exception.RethrowingFactoryExceptionHandler;
import de.factoryfx.factory.log.FactoryUpdateLog;
import de.factoryfx.factory.testfactories.ExampleFactoryA;
import de.factoryfx.factory.testfactories.ExampleFactoryB;
import de.factoryfx.factory.testfactories.ExampleLiveObjectA;
import de.factoryfx.factory.testfactories.ExampleLiveObjectB;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ApplicationServerTest {

    private static class ChangeListingSummary{
        public final List<String> changedIds;

        public ChangeListingSummary(List<String> changedIds) {
            this.changedIds = changedIds;
        }
    }

    @Test
    public void test_summary() throws Exception {
        ExampleFactoryA root = new ExampleFactoryA();
        root =root.internal().prepareUsableCopy();

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

        ApplicationServer<Void,ExampleLiveObjectA,ExampleFactoryA,ChangeListingSummary> applicationServer = new ApplicationServer<>(new FactoryManager<>(new RethrowingFactoryExceptionHandler<>()), memoryFactoryStorage);
        applicationServer.start();

        Thread.sleep(2);//avoid same timestamp
        {
            final DataAndNewMetadata<ExampleFactoryA> prepareNewFactory = applicationServer.prepareNewFactory();
            prepareNewFactory.root.stringAttribute.set("change1");
            applicationServer.updateCurrentFactory(prepareNewFactory, "user", "comment1",(p)->true);
        }
        Thread.sleep(2);//avoid same timestamp
        {
            final DataAndNewMetadata<ExampleFactoryA> prepareNewFactory = applicationServer.prepareNewFactory();
            prepareNewFactory.root.stringAttribute.set("change2");
            applicationServer.updateCurrentFactory(prepareNewFactory, "user", "comment2",(p)->true);
        }
        Thread.sleep(2);//avoid same timestamp
        {
            final DataAndNewMetadata<ExampleFactoryA> prepareNewFactory = applicationServer.prepareNewFactory();
            prepareNewFactory.root.stringAttribute.set("change3");
            applicationServer.updateCurrentFactory(prepareNewFactory, "user", "comment3",(p)->true);
        }
        Thread.sleep(2);//avoid same timestamp



        final List<StoredDataMetadata<ChangeListingSummary>> historyFactoryList = new ArrayList<>(applicationServer.getHistoryFactoryList());
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
        root =root.internal().prepareUsableCopy();
        final InMemoryDataStorage<ExampleFactoryA,Void> memoryFactoryStorage = new InMemoryDataStorage<>(root);
        memoryFactoryStorage.loadInitialFactory();

        ApplicationServer<Void,ExampleLiveObjectA,ExampleFactoryA,Void> applicationServer = new ApplicationServer<>(new FactoryManager<>(new RethrowingFactoryExceptionHandler<>()), memoryFactoryStorage);
        applicationServer.start();
        DataAndNewMetadata<ExampleFactoryA> editableFactory = applicationServer.prepareNewFactory();
        editableFactory.root.referenceListAttribute.add(new ExampleFactoryB());

        FactoryUpdateLog<ExampleFactoryA> log = applicationServer.updateCurrentFactory(editableFactory,"","", x->true);
        AttributeDiffInfo theDiff = log.mergeDiffInfo.mergeInfos.get(0);

        String dt = theDiff.getAttributeDisplayText(log.mergeDiffInfo.getPreviousRootData());
        String dtNew = theDiff.getAttributeDisplayText(log.mergeDiffInfo.getNewRootData());
        Assert.assertNotEquals(dt,dtNew);

    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_history() throws Exception {
        ExampleFactoryA root = new ExampleFactoryA();
        root =root.internal().prepareUsableCopy();
        final InMemoryDataStorage<ExampleFactoryA, Void> memoryFactoryStorage = new InMemoryDataStorage<>(root);
        memoryFactoryStorage.loadInitialFactory();
        Thread.sleep(2);//avoid same timestamp
        {
            final DataAndNewMetadata<ExampleFactoryA> prepareNewFactory = memoryFactoryStorage.getPrepareNewFactory();
            prepareNewFactory.root.stringAttribute.set("change1");
            memoryFactoryStorage.updateCurrentFactory(prepareNewFactory, "user", "comment1",null);
        }
        Thread.sleep(2);//avoid same timestamp
        {
            final DataAndNewMetadata<ExampleFactoryA> prepareNewFactory = memoryFactoryStorage.getPrepareNewFactory();
            prepareNewFactory.root.stringAttribute.set("change2");
            memoryFactoryStorage.updateCurrentFactory(prepareNewFactory, "user", "comment2",null);
        }
        Thread.sleep(2);//avoid same timestamp
        {
            final DataAndNewMetadata<ExampleFactoryA> prepareNewFactory = memoryFactoryStorage.getPrepareNewFactory();
            prepareNewFactory.root.stringAttribute.set("change3");
            memoryFactoryStorage.updateCurrentFactory(prepareNewFactory, "user", "comment3",null);
        }
        Thread.sleep(2);//avoid same timestamp

        ApplicationServer<Void,ExampleLiveObjectA,ExampleFactoryA,Void> applicationServer = new ApplicationServer<>(Mockito.mock(FactoryManager.class), memoryFactoryStorage);

        final List<StoredDataMetadata<Void>> historyFactoryList = new ArrayList<>(applicationServer.getHistoryFactoryList());
        Collections.sort(historyFactoryList, (o1, o2) -> Objects.compare(o1.creationTime, o2.creationTime, Comparator.reverseOrder()));

        Assert.assertEquals(4,historyFactoryList.size());
        Assert.assertEquals("change1",applicationServer.getHistoryFactory(historyFactoryList.get(2).id).stringAttribute.get());
        Assert.assertEquals("change2",applicationServer.getHistoryFactory(historyFactoryList.get(1).id).stringAttribute.get());
        Assert.assertEquals("change3",applicationServer.getHistoryFactory(historyFactoryList.get(0).id).stringAttribute.get());
    }


    public static class ExampleFactoryARecreation extends SimpleFactoryBase<ExampleLiveObjectA,Void> {
        public final FactoryReferenceAttribute<ExampleLiveObjectB,ExampleFactoryBRecreation> referenceAttribute = new FactoryReferenceAttribute<>(ExampleFactoryBRecreation.class).labelText("ExampleA2");

        @Override
        public ExampleLiveObjectA createImpl() {
            return null;
        }
    }

    public static class ExampleFactoryBRecreation extends FactoryBase<ExampleLiveObjectB,Void> {
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
        root =root.internal().prepareUsableCopy();
        final InMemoryDataStorage<ExampleFactoryARecreation, Void> memoryFactoryStorage = new InMemoryDataStorage<>(root);
        ApplicationServer<Void,ExampleLiveObjectA,ExampleFactoryARecreation,Void> applicationServer = new ApplicationServer<>(new FactoryManager<>(new RethrowingFactoryExceptionHandler<>()), memoryFactoryStorage);

        applicationServer.start();

        DataAndNewMetadata<ExampleFactoryARecreation> update = applicationServer.prepareNewFactory();

        update.root.referenceAttribute.set(new ExampleFactoryBRecreation());

        Assert.assertEquals(0,update.root.referenceAttribute.get().recreationCounter);
        applicationServer.updateCurrentFactory(update, "", "", s -> true);
        Assert.assertEquals(0,update.root.referenceAttribute.get().recreationCounter);
    }


}