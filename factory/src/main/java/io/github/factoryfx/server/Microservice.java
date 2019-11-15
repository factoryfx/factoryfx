package io.github.factoryfx.server;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

import io.github.factoryfx.factory.builder.FactoryTreeBuilder;
import io.github.factoryfx.factory.merge.AttributeDiffInfo;
import io.github.factoryfx.factory.merge.DataMerger;
import io.github.factoryfx.factory.merge.MergeDiffInfo;
import io.github.factoryfx.factory.storage.*;
import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.FactoryManager;
import io.github.factoryfx.factory.RootFactoryWrapper;
import io.github.factoryfx.factory.log.FactoryUpdateLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * starting point for factoryfx application
 *
 * @param <R> Root
 */
public class Microservice<L,R extends FactoryBase<L,R>> {
    private static final Logger logger = LoggerFactory.getLogger(Microservice.class);

    private final FactoryManager<L,R> factoryManager;
    private final DataStorage<R> dataStorage;
    private final FactoryTreeBuilder<L,R> factoryTreeBuilder;

    public Microservice(FactoryManager<L,R> factoryManager, DataStorage<R> dataStorage, FactoryTreeBuilder<L,R> factoryTreeBuilder) {
        this.factoryManager = factoryManager;
        this.dataStorage = dataStorage;
        this.factoryTreeBuilder = factoryTreeBuilder;
    }

    public MergeDiffInfo<R> getDiffToPreviousVersion(StoredDataMetadata storedDataMetadata) {
        R historyCurrent = getHistoryFactory(storedDataMetadata.mergerVersionId);
        R historyCommon = getHistoryFactory(storedDataMetadata.baseVersionId);
        R historyUpdate = getHistoryFactory(storedDataMetadata.id);
        return new DataMerger<>(historyCurrent,historyCommon,historyUpdate).createMergeResult((permission)->true).executeMerge();
    }

    public FactoryUpdateLog<R> revertTo(StoredDataMetadata storedDataMetadata, String user) {
        R historyFactory = getHistoryFactory(storedDataMetadata.id);
        DataAndId<R> currentFactory = dataStorage.getCurrentData();
        return updateCurrentFactory(new DataUpdate<>(
                historyFactory,
                user,
                "revert to: "+storedDataMetadata.id,
                currentFactory.id)
        );
    }

    private UpdateSummary createUpdateSummary(MergeDiffInfo<R> mergeDiffInfo){
        return new UpdateSummary(mergeDiffInfo.mergeInfos);
    }

    public synchronized FactoryUpdateLog<R> updateCurrentFactory(DataUpdate<R> update) {
        R commonVersion = dataStorage.getHistoryData(update.baseVersionId);
        FactoryUpdateLog<R> factoryLog = factoryManager.update(commonVersion,update.root, update.permissionChecker);
        if (!factoryLog.failedUpdate() && factoryLog.successfullyMerged()){

            UpdateSummary changeSummary=null;
            if (factoryLog.mergeDiffInfo!=null){
                changeSummary=createUpdateSummary(factoryLog.mergeDiffInfo);
            }

            R copy = factoryManager.getCurrentFactory().utility().copy();
            DataUpdate<R> updateAfterMerge = new DataUpdate<>(
                    copy,
                    update.user,
                    update.comment,
                    update.baseVersionId
            );
            dataStorage.updateCurrentData(updateAfterMerge,changeSummary);
        }
        return factoryLog;
    }


    public synchronized MergeDiffInfo<R> simulateUpdateCurrentFactory(DataUpdate<R> possibleUpdate){
        R commonVersion = dataStorage.getHistoryData(possibleUpdate.baseVersionId);
        return factoryManager.simulateUpdate(commonVersion , possibleUpdate.root, possibleUpdate.permissionChecker);
    }

    /**
     *  prepare a new factory which could be used to update data. mainly give it the correct baseVersionId
     *  @return new possible factory update with prepared ids/metadata
     * */
    public synchronized DataUpdate<R> prepareNewFactory() {
        if (!factoryManager.isStarted()){
           throw new IllegalStateException("Microservice is not started");
        }
        return prepareNewFactory("","");
    }

    /**
     *  prepare a new factory which could be used to update data. mainly give it the correct baseVersionId
     * @param user use
     * @param comment comment
     * @return new possible factory update with prepared ids/metadata
     */
    public synchronized DataUpdate<R> prepareNewFactory(String user, String comment) {
        DataAndId<R> currentFactory = dataStorage.getCurrentData();//TODO optimise we need just the id
        return new DataUpdate<>(
                factoryManager.getCurrentFactory().utility().copy(),
                user,
                comment,
                currentFactory.id);
    }


    public R getHistoryFactory(String id) {
        return dataStorage.getHistoryData(id);
    }

    public Collection<StoredDataMetadata> getHistoryFactoryList() {
        return dataStorage.getHistoryDataList();
    }

    public synchronized L start() {
        final DataAndId<R> currentFactory = dataStorage.getCurrentData();
        R currentFactoryRoot = currentFactory.root.internal().finalise();

        if (factoryTreeBuilder.isPersistentFactoryBuilder()){
            R initialData = dataStorage.getInitialData();
            if (initialData!=null){
                initialData.internal().finalise();
                R rebuildRoot = factoryTreeBuilder.rebuildTreeUnvalidated(initialData);
                DataMerger<R> merge = new DataMerger<>(currentFactoryRoot,initialData,rebuildRoot);
                MergeDiffInfo<R> mergeDiffInfo = merge.createMergeResult((p) -> true).executeMerge();

                if (mergeDiffInfo.successfullyMerged()){
                    DataUpdate<R> dataUpdate = new DataUpdate<>(currentFactoryRoot,"System","FactoryTreeBuilder update",currentFactory.id);
                    dataStorage.updateCurrentData(dataUpdate,new UpdateSummary(mergeDiffInfo.mergeInfos));
                } else {
                    logger.warn("can't apply changes from FactoryTreeBuilder");

                    Map<UUID, FactoryBase<?, R>> oldMap = currentFactoryRoot.internal().collectChildFactoryMap();
                    Map<UUID, FactoryBase<?, R>> newMap = currentFactoryRoot.internal().collectChildFactoryMap();
                    for (AttributeDiffInfo conflictInfo : mergeDiffInfo.conflictInfos) {
                        logger.warn("Conflict "+ conflictInfo.getDiffDisplayText(oldMap,newMap));
                    }
                }
            }
        }


        currentFactoryRoot.internal().setMicroservice(this);//also mind ExceptionResponseAction#reset
        currentFactoryRoot.internal().setFactoryTreeBuilder(factoryTreeBuilder);
        return factoryManager.start(new RootFactoryWrapper<>(currentFactoryRoot));
    }

    public synchronized void stop() {
        factoryManager.stop();
    }

    public L getRootLiveObject(){
        return factoryManager.getCurrentFactory().internal().getLiveObject();
    }


}
