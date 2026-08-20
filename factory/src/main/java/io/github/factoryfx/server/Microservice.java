package io.github.factoryfx.server;

import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

import io.github.factoryfx.factory.FactoryUpdate;
import io.github.factoryfx.factory.builder.FactoryTreeBuilder;
import io.github.factoryfx.factory.merge.AttributeDiffInfo;
import io.github.factoryfx.factory.merge.DataMerger;
import io.github.factoryfx.factory.merge.MergeDiffInfo;
import io.github.factoryfx.factory.storage.*;
import io.github.factoryfx.factory.storage.migration.MigrationManager;
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
    private final MigrationManager<R> migrationManager;
    private final MicroserviceDeployment<L,R> deployment;

    public Microservice(FactoryManager<L,R> factoryManager, DataStorage<R> dataStorage, FactoryTreeBuilder<L,R> factoryTreeBuilder) {
        this(factoryManager, dataStorage, factoryTreeBuilder, null);
    }

    public Microservice(FactoryManager<L,R> factoryManager, DataStorage<R> dataStorage, FactoryTreeBuilder<L,R> factoryTreeBuilder, MigrationManager<R> migrationManager) {
        this.factoryManager = factoryManager;
        this.dataStorage = dataStorage;
        this.factoryTreeBuilder = factoryTreeBuilder;
        this.migrationManager = migrationManager;
        this.deployment = new MicroserviceDeployment<>(this, factoryManager, dataStorage, factoryTreeBuilder, migrationManager);
    }

    /**
     * deployment tooling: preflight check before switching to a new software version, configuration snapshots and
     * explicit persistence of the registered configuration patches. intended for deployment scripts/tooling, not for
     * the running application.
     *
     * @return deployment tooling for this microservice
     */
    public MicroserviceDeployment<L,R> deployment() {
        return deployment;
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
                "revert to configuration of timestamp: "+ DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(storedDataMetadata.creationTime) +
                        ", original comment: " + storedDataMetadata.comment,
                currentFactory.id)
        );
    }

    private UpdateSummary createUpdateSummary(MergeDiffInfo<R> mergeDiffInfo){
        return new UpdateSummary(mergeDiffInfo.mergeInfos);
    }

    public synchronized FactoryUpdateLog<R> updateCurrentFactory(DataUpdate<R> update) {
        //update based on the current configuration (the usual case): the common version is the currently running
        //factory tree, an in-memory copy avoids reading and deserializing it from the storage
        boolean baseVersionIsCurrent = factoryManager.isStarted() && update.baseVersionId.equals(dataStorage.getCurrentDataId());
        R commonVersion = baseVersionIsCurrent
                ? factoryManager.getCurrentFactory().utility().copy()
                : dataStorage.getHistoryData(update.baseVersionId);
        FactoryUpdateLog<R> factoryLog = factoryManager.update(commonVersion,update.root, update.permissionChecker, baseVersionIsCurrent);
        if (!factoryLog.failedUpdate() && factoryLog.successfullyMerged()){

            UpdateSummary changeSummary=null;
            if (factoryLog.mergeDiffInfo!=null){
                changeSummary=createUpdateSummary(factoryLog.mergeDiffInfo);
            }

            DataUpdate<R> updateAfterMerge = new DataUpdate<>(
                    factoryManager.getCurrentFactory(),
                    update.user,
                    update.comment,
                    update.baseVersionId
            );
            dataStorage.updateCurrentData(updateAfterMerge,changeSummary);
        }
        return factoryLog;
    }


    public synchronized MergeDiffInfo<R> simulateUpdateCurrentFactory(DataUpdate<R> possibleUpdate){
        boolean baseVersionIsCurrent = factoryManager.isStarted() && possibleUpdate.baseVersionId.equals(dataStorage.getCurrentDataId());
        R commonVersion = baseVersionIsCurrent
                ? factoryManager.getCurrentFactory().utility().copy()
                : dataStorage.getHistoryData(possibleUpdate.baseVersionId);
        return factoryManager.simulateUpdate(commonVersion , possibleUpdate.root, possibleUpdate.permissionChecker, baseVersionIsCurrent);
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
     *  Update from different process(browser java richclient)
     *  prepare a new factory which could be used to update data. mainly give it the correct baseVersionId
     * @param user use
     * @param comment comment
     * @return new possible factory update with prepared ids/metadata
     */
    public synchronized DataUpdate<R> prepareNewFactory(String user, String comment) {
        return new DataUpdate<>(
                factoryManager.getCurrentFactory().utility().copy(),
                user,
                comment,
                dataStorage.getCurrentDataId());
    }


    public R getHistoryFactory(String id) {
        return dataStorage.getHistoryData(id);
    }

    public Collection<StoredDataMetadata> getHistoryFactoryList(boolean light) {
        return dataStorage.getHistoryDataList(light);
    }

    public synchronized L start() {
        final DataAndId<R> currentFactory = dataStorage.getCurrentData();
        R currentFactoryRoot = currentFactory.root.internal().finalise();
        currentFactoryRoot.internal().setFactoryTreeBuilder(factoryTreeBuilder);

        if (factoryTreeBuilder.isPersistentFactoryBuilder()){
            //the current configuration is the reference: existing factories keep their values and wiring,
            //the FactoryTreeBuilder describes the technical configuration of the tree and only contributes newly introduced factories
            if (currentFactoryRoot.internal().getTreeBuilderName()!=null || currentFactoryRoot.internal().isTreeBuilderClassUsed()){
                R rebuildRoot = factoryTreeBuilder.rebuildTreeForExistingConfiguration(currentFactoryRoot);
                DataMerger<R> merge = new DataMerger<>(currentFactoryRoot,currentFactoryRoot.utility().copy(),rebuildRoot);
                MergeDiffInfo<R> mergeDiffInfo = merge.createMergeResult((p) -> true, true).executeMerge();

                if (mergeDiffInfo.successfullyMerged()){
                    if (!mergeDiffInfo.mergeInfos.isEmpty()){
                        DataUpdate<R> dataUpdate = new DataUpdate<>(currentFactoryRoot,"System","FactoryTreeBuilder update",currentFactory.id);
                        dataStorage.updateCurrentData(dataUpdate,new UpdateSummary(mergeDiffInfo.mergeInfos));
                    }
                } else {
                    logger.warn("can't apply changes from FactoryTreeBuilder to current storage Data");

                    Map<UUID, FactoryBase<?, R>> oldMap = currentFactoryRoot.internal().collectChildFactoryMap();
                    Map<UUID, FactoryBase<?, R>> newMap = currentFactoryRoot.internal().collectChildFactoryMap();
                    for (AttributeDiffInfo conflictInfo : mergeDiffInfo.conflictInfos) {
                        logger.warn("Conflict: "+ conflictInfo.getDiffDisplayText(oldMap,newMap));
                    }
                }
            }
        }


        currentFactoryRoot.internal().setMicroservice(this);//also mind ExceptionResponseAction#reset
        return factoryManager.start(new RootFactoryWrapper<>(currentFactoryRoot));
    }

    public synchronized void stop() {
        factoryManager.stop();
    }

    public L getRootLiveObject(){
        return factoryManager.getCurrentFactory().internal().getLiveObject();
    }

    /**
     * updates the current factories from the same process(jvm)
     * @param updater update execution
     */
    public void update(FactoryUpdate<R> updater){
        factoryManager.update(updater);
        dataStorage.updateCurrentData(new DataUpdate<>(factoryManager.getCurrentFactory(),"system","",dataStorage.getCurrentDataId()),null);
    }

}
