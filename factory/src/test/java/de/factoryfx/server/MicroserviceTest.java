package de.factoryfx.server;

import de.factoryfx.data.merge.AttributeDiffInfo;
import de.factoryfx.data.merge.MergeDiffInfo;
import de.factoryfx.data.storage.DataAndStoredMetadata;
import de.factoryfx.data.storage.StoredDataMetadata;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.factory.builder.FactoryTreeBuilder;
import de.factoryfx.factory.builder.MicroserviceBuilder;
import de.factoryfx.factory.builder.Scope;
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
        FactoryTreeBuilder<Void,ExampleLiveObjectA,ExampleFactoryA,ChangeListingSummary> builder = new FactoryTreeBuilder<>(ExampleFactoryA.class);
        builder.addFactory(ExampleFactoryA.class, Scope.SINGLETON, ctx -> new ExampleFactoryA());

        Microservice<Void,ExampleLiveObjectA,ExampleFactoryA,ChangeListingSummary> microservice = builder.microservice().withInMemoryStorage().widthChangeSummaryCreator(mergeDiffInfo -> {
            if (mergeDiffInfo==null){
                return null;
            }
            return new ChangeListingSummary(mergeDiffInfo.mergeInfos.stream().map((m)->m.dataId).collect(Collectors.toList()));
        }).build();
        microservice.start();

        String rootId=microservice.prepareNewFactory().root.getId();

        Thread.sleep(2);//avoid same timestamp
        {
            final DataAndStoredMetadata<ExampleFactoryA,ChangeListingSummary> prepareNewFactory = microservice.prepareNewFactory();
            prepareNewFactory.root.stringAttribute.set("change1");
            microservice.updateCurrentFactory(prepareNewFactory);
        }
        Thread.sleep(2);//avoid same timestamp
        {
            final DataAndStoredMetadata<ExampleFactoryA,ChangeListingSummary> prepareNewFactory = microservice.prepareNewFactory();
            prepareNewFactory.root.stringAttribute.set("change2");
            microservice.updateCurrentFactory(prepareNewFactory);
        }
        Thread.sleep(2);//avoid same timestamp
        {
            final DataAndStoredMetadata<ExampleFactoryA,ChangeListingSummary> prepareNewFactory = microservice.prepareNewFactory();
            prepareNewFactory.root.stringAttribute.set("change3");
            microservice.updateCurrentFactory(prepareNewFactory);
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

        FactoryTreeBuilder<Void,ExampleLiveObjectA,ExampleFactoryA,Void> builder = new FactoryTreeBuilder<>(ExampleFactoryA.class);
        builder.addFactory(ExampleFactoryA.class, Scope.SINGLETON, ctx -> {
            return new ExampleFactoryA();
        });

        Microservice<Void,ExampleLiveObjectA,ExampleFactoryA,Void> microservice = builder.microservice().withInMemoryStorage().build();

        microservice.start();
        DataAndStoredMetadata<ExampleFactoryA,Void> editableFactory = microservice.prepareNewFactory();
        editableFactory.root.referenceListAttribute.add(new ExampleFactoryB());

        FactoryUpdateLog<ExampleFactoryA> log = microservice.updateCurrentFactory(editableFactory);
        AttributeDiffInfo theDiff = log.mergeDiffInfo.mergeInfos.get(0);

        String dt = theDiff.getAttributeDisplayText(log.mergeDiffInfo.getPreviousRootData());
        String dtNew = theDiff.getAttributeDisplayText(log.mergeDiffInfo.getNewRootData());
        Assert.assertNotEquals(dt,dtNew);

    }

    @Test
    public void test_history() throws Exception {
        FactoryTreeBuilder<Void,ExampleLiveObjectA,ExampleFactoryA,Void> builder = new FactoryTreeBuilder<>(ExampleFactoryA.class);
        builder.addFactory(ExampleFactoryA.class, Scope.SINGLETON, ctx -> new ExampleFactoryA());
        Microservice<Void,ExampleLiveObjectA,ExampleFactoryA,Void> microservice = builder.microservice().withInMemoryStorage().build();
        microservice.start();

        Thread.sleep(2);//avoid same timestamp
        {
            final DataAndStoredMetadata<ExampleFactoryA,Void> prepareNewFactory = microservice.prepareNewFactory();
            prepareNewFactory.root.stringAttribute.set("change1");
            microservice.updateCurrentFactory(prepareNewFactory);
        }
        Thread.sleep(2);//avoid same timestamp
        {
            final DataAndStoredMetadata<ExampleFactoryA,Void> prepareNewFactory = microservice.prepareNewFactory();
            prepareNewFactory.root.stringAttribute.set("change2");
            microservice.updateCurrentFactory(prepareNewFactory);
        }
        Thread.sleep(2);//avoid same timestamp
        {
            final DataAndStoredMetadata<ExampleFactoryA,Void> prepareNewFactory = microservice.prepareNewFactory();
            prepareNewFactory.root.stringAttribute.set("change3");
            microservice.updateCurrentFactory(prepareNewFactory);
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
        public final FactoryReferenceAttribute<ExampleLiveObjectB,ExampleFactoryBRecreation> referenceAttribute = new FactoryReferenceAttribute<>(ExampleFactoryBRecreation.class).labelText("ExampleA2").nullable();

        @Override
        public ExampleLiveObjectA createImpl() {
            return null;
        }
    }

    public static class ExampleFactoryBRecreation extends FactoryBase<ExampleLiveObjectB,Void,ExampleFactoryARecreation> {
        public final FactoryReferenceAttribute<ExampleLiveObjectB,ExampleFactoryB> referenceAttribute = new FactoryReferenceAttribute<>(ExampleFactoryB.class).labelText("ExampleA2").nullable();

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
        FactoryTreeBuilder<Void,ExampleLiveObjectA,ExampleFactoryARecreation,Void> builder = new FactoryTreeBuilder<>(ExampleFactoryARecreation.class);
        builder.addFactory(ExampleFactoryARecreation.class, Scope.SINGLETON, ctx -> new ExampleFactoryARecreation());
        Microservice<Void,ExampleLiveObjectA,ExampleFactoryARecreation,Void> microservice = builder.microservice().withInMemoryStorage().build();

        microservice.start();

        DataAndStoredMetadata<ExampleFactoryARecreation,Void> update = microservice.prepareNewFactory();

        update.root.referenceAttribute.set(new ExampleFactoryBRecreation());

        Assert.assertEquals(0,update.root.referenceAttribute.get().recreationCounter);
        microservice.updateCurrentFactory(update);
        Assert.assertEquals(0,update.root.referenceAttribute.get().recreationCounter);
    }


    public static class BrokenFactory extends FactoryBase<Void,Void,BrokenFactory> {
        public BrokenFactory(){
            configLifeCycle().setCreator(() -> {
                throw new RuntimeException("create");
            });
        }
    }

    @Test(expected = RuntimeException.class)
    public void test_create_width_exception() throws Exception {
        FactoryTreeBuilder<Void,Void,BrokenFactory,ChangeListingSummary> builder = new FactoryTreeBuilder<>(BrokenFactory.class);
        builder.addFactory(BrokenFactory.class, Scope.SINGLETON, ctx -> new BrokenFactory());
        Microservice<Void,Void,BrokenFactory,ChangeListingSummary> microservice = builder.microservice().withInMemoryStorage().build();
        microservice.start();
    }


    @Test
    public void test_prepareNewFactory_is_copy() {
        FactoryTreeBuilder<Void,ExampleLiveObjectA,ExampleFactoryA,Void> builder = new FactoryTreeBuilder<>(ExampleFactoryA.class);
        builder.addFactory(ExampleFactoryA.class, Scope.SINGLETON, ctx -> new ExampleFactoryA());
        Microservice<Void,ExampleLiveObjectA,ExampleFactoryA,Void> microservice = builder.microservice().withInMemoryStorage().build();

        Assert.assertFalse(microservice.prepareNewFactory().root==microservice.prepareNewFactory().root);
        Assert.assertFalse(microservice.prepareNewFactory().root.referenceListAttribute==microservice.prepareNewFactory().root.referenceListAttribute);
    }

    @Test
    public void test_getDiffToPreviousVersion() {
        FactoryTreeBuilder<Void,ExampleLiveObjectA,ExampleFactoryA,Void> builder = new FactoryTreeBuilder<>(ExampleFactoryA.class);
        builder.addFactory(ExampleFactoryA.class, Scope.SINGLETON, ctx -> new ExampleFactoryA());
        Microservice<Void,ExampleLiveObjectA,ExampleFactoryA,Void> microservice = builder.microservice().withInMemoryStorage().build();

        microservice.start();
        Assert.assertEquals(0,microservice.prepareNewFactory().root.referenceListAttribute.size());
        Assert.assertEquals(0,microservice.getHistoryFactory(new ArrayList<>(microservice.getHistoryFactoryList()).get(0).id).referenceListAttribute.size());

        DataAndStoredMetadata<ExampleFactoryA,Void> update = microservice.prepareNewFactory();
        update.root.referenceListAttribute.add(new ExampleFactoryB());
        FactoryUpdateLog<ExampleFactoryA> log = microservice.updateCurrentFactory(update);

        Assert.assertEquals(1,log.mergeDiffInfo.mergeInfos.size());
//        Assert.assertEquals(1,microservice.prepareNewFactory().root.referenceListAttribute.size());

        Assert.assertEquals(2,microservice.getHistoryFactoryList().size());
        List<StoredDataMetadata<Void>> historyFactoryList = new ArrayList<>(microservice.getHistoryFactoryList());

        historyFactoryList.sort(Comparator.comparing(o -> o.creationTime));
        Assert.assertEquals(0,microservice.getHistoryFactory(historyFactoryList.get(0).id).referenceListAttribute.size());
        Assert.assertEquals(1,microservice.getHistoryFactory(historyFactoryList.get(1).id).referenceListAttribute.size());
        MergeDiffInfo<ExampleFactoryA> diffToPreviousVersion = microservice.getDiffToPreviousVersion(historyFactoryList.get(1));

        Assert.assertEquals(1,diffToPreviousVersion.mergeInfos.size());


    }
}