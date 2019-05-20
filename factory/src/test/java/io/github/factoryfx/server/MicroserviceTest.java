package io.github.factoryfx.server;

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
import java.util.stream.Collectors;

public class MicroserviceTest {

    private static class ChangeListingSummary{
        public final List<UUID> changedIds;

        public ChangeListingSummary(List<UUID> changedIds) {
            this.changedIds = changedIds;
        }
    }

    @Test
    public void test_summary() throws Exception {
        FactoryTreeBuilder<ExampleLiveObjectA,ExampleFactoryA,ChangeListingSummary> builder = new FactoryTreeBuilder<>(ExampleFactoryA.class, ctx -> new ExampleFactoryA());

        Microservice<ExampleLiveObjectA,ExampleFactoryA,ChangeListingSummary> microservice = builder.microservice().withChangeSummaryCreator(mergeDiffInfo -> {
            if (mergeDiffInfo==null){
                return null;
            }
            return new ChangeListingSummary(mergeDiffInfo.mergeInfos.stream().map((m)->m.dataId).collect(Collectors.toList()));
        }).build();
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



        final List<StoredDataMetadata<ChangeListingSummary>> historyFactoryList = new ArrayList<>(microservice.getHistoryFactoryList());
        Collections.sort(historyFactoryList, (o1, o2) -> Objects.compare(o1.creationTime, o2.creationTime, Comparator.reverseOrder()));



        Assertions.assertEquals(4,historyFactoryList.size());
        Assertions.assertEquals(1,historyFactoryList.get(2).changeSummary.changedIds.size());
        Assertions.assertEquals(rootId,historyFactoryList.get(2).changeSummary.changedIds.get(0));
        Assertions.assertEquals(1,historyFactoryList.get(1).changeSummary.changedIds.size());
        Assertions.assertEquals(rootId,historyFactoryList.get(1).changeSummary.changedIds.get(0));
        Assertions.assertEquals(1,historyFactoryList.get(2).changeSummary.changedIds.size());
        Assertions.assertEquals(rootId,historyFactoryList.get(0).changeSummary.changedIds.get(0));
    }

    @Test
    public void testUpdateReferenceList() {
        ExampleFactoryA root = new ExampleFactoryA();
        root.referenceListAttribute.add(new ExampleFactoryB());
        root =root.internal().finalise();

        FactoryTreeBuilder<ExampleLiveObjectA,ExampleFactoryA,Void> builder = new FactoryTreeBuilder<>(ExampleFactoryA.class, ctx -> {
            return new ExampleFactoryA();
        });

        Microservice<ExampleLiveObjectA,ExampleFactoryA,Void> microservice = builder.microservice().build();

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
        FactoryTreeBuilder<ExampleLiveObjectA,ExampleFactoryA,Void> builder = new FactoryTreeBuilder<>(ExampleFactoryA.class, ctx -> new ExampleFactoryA());
        Microservice<ExampleLiveObjectA,ExampleFactoryA,Void> microservice = builder.microservice().build();
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



        final List<StoredDataMetadata<Void>> historyFactoryList = new ArrayList<>(microservice.getHistoryFactoryList());
        Collections.sort(historyFactoryList, (o1, o2) -> Objects.compare(o1.creationTime, o2.creationTime, Comparator.reverseOrder()));

        Assertions.assertEquals(4,historyFactoryList.size());
        Assertions.assertEquals("change1", microservice.getHistoryFactory(historyFactoryList.get(2).id).stringAttribute.get());
        Assertions.assertEquals("change2", microservice.getHistoryFactory(historyFactoryList.get(1).id).stringAttribute.get());
        Assertions.assertEquals("change3", microservice.getHistoryFactory(historyFactoryList.get(0).id).stringAttribute.get());
    }


    public static class ExampleFactoryARecreation extends SimpleFactoryBase<ExampleLiveObjectA,ExampleFactoryARecreation> {
        public final FactoryAttribute<ExampleFactoryARecreation,ExampleLiveObjectB,ExampleFactoryBRecreation> referenceAttribute = new FactoryAttribute<ExampleFactoryARecreation,ExampleLiveObjectB,ExampleFactoryBRecreation>().labelText("ExampleA2").nullable();

        @Override
        public ExampleLiveObjectA createImpl() {
            return null;
        }
    }

    public static class ExampleFactoryBRecreation extends FactoryBase<ExampleLiveObjectB,ExampleFactoryARecreation> {
        public final FactoryAttribute<ExampleFactoryARecreation,ExampleLiveObjectB,ExampleFactoryBRecreation> referenceAttribute = new FactoryAttribute<ExampleFactoryARecreation,ExampleLiveObjectB,ExampleFactoryBRecreation>().labelText("ExampleA2").nullable();

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
        FactoryTreeBuilder<ExampleLiveObjectA,ExampleFactoryARecreation,Void> builder = new FactoryTreeBuilder<>(ExampleFactoryARecreation.class, ctx -> new ExampleFactoryARecreation());
        Microservice<ExampleLiveObjectA,ExampleFactoryARecreation,Void> microservice = builder.microservice().build();

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
            FactoryTreeBuilder< Void, BrokenFactory, ChangeListingSummary> builder = new FactoryTreeBuilder<>(BrokenFactory.class, ctx -> new BrokenFactory());
            Microservice<Void, BrokenFactory, ChangeListingSummary> microservice = builder.microservice().build();
            microservice.start();
        });
    }


    @Test
    public void test_prepareNewFactory_is_copy() {
        FactoryTreeBuilder<ExampleLiveObjectA,ExampleFactoryA,Void> builder = new FactoryTreeBuilder<>(ExampleFactoryA.class, ctx -> new ExampleFactoryA());
        Microservice<ExampleLiveObjectA,ExampleFactoryA,Void> microservice = builder.microservice().build();
        microservice.start();

        Assertions.assertFalse(microservice.prepareNewFactory().root==microservice.prepareNewFactory().root);
        Assertions.assertFalse(microservice.prepareNewFactory().root.referenceListAttribute==microservice.prepareNewFactory().root.referenceListAttribute);
    }

    @Test
    public void test_getDiffToPreviousVersion() {
        FactoryTreeBuilder<ExampleLiveObjectA,ExampleFactoryA,Void> builder = new FactoryTreeBuilder<>(ExampleFactoryA.class, ctx -> new ExampleFactoryA());
        Microservice<ExampleLiveObjectA,ExampleFactoryA,Void> microservice = builder.microservice().build();

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
        List<StoredDataMetadata<Void>> historyFactoryList = new ArrayList<>(microservice.getHistoryFactoryList());
        historyFactoryList.sort(Comparator.comparing(o -> o.comment));
        Collections.reverse(historyFactoryList);

        Assertions.assertEquals(0,microservice.getHistoryFactory(historyFactoryList.get(0).id).referenceListAttribute.size());
        Assertions.assertEquals(1,microservice.getHistoryFactory(historyFactoryList.get(1).id).referenceListAttribute.size());
        MergeDiffInfo<ExampleFactoryA> diffToPreviousVersion = microservice.getDiffToPreviousVersion(historyFactoryList.get(1));

        Assertions.assertEquals(1,diffToPreviousVersion.mergeInfos.size());


    }
}