package io.github.factoryfx.server;

import java.util.Collection;

import io.github.factoryfx.factory.builder.FactoryTreeBuilder;
import io.github.factoryfx.factory.merge.DataMerger;
import io.github.factoryfx.factory.merge.MergeDiffInfo;
import io.github.factoryfx.factory.storage.*;
import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.FactoryManager;
import io.github.factoryfx.factory.RootFactoryWrapper;
import io.github.factoryfx.factory.log.FactoryUpdateLog;

/**
 * starting point for factoryfx application
 *
 * @param <R> Root
 * @param <S> Summary Data for factory history
 */
public class Microservice<L,R extends FactoryBase<L,R>,S> {
    private final FactoryManager<L,R> factoryManager;
    private final DataStorage<R,S> dataStorage;
    private final ChangeSummaryCreator<R,S> changeSummaryCreator;
    private final FactoryTreeBuilder<L,R,S> factoryTreeBuilder;

    public Microservice(FactoryManager<L,R> factoryManager, DataStorage<R,S> dataStorage, ChangeSummaryCreator<R,S> changeSummaryCreator, FactoryTreeBuilder<L,R,S> factoryTreeBuilder) {
        this.factoryManager = factoryManager;
        this.dataStorage = dataStorage;
        this.changeSummaryCreator = changeSummaryCreator;
        this.factoryTreeBuilder = factoryTreeBuilder;
    }

    public MergeDiffInfo<R> getDiffToPreviousVersion(StoredDataMetadata<S> storedDataMetadata) {
        R historyCurrent = getHistoryFactory(storedDataMetadata.mergerVersionId);
        R historyCommon = getHistoryFactory(storedDataMetadata.baseVersionId);
        R historyUpdate = getHistoryFactory(storedDataMetadata.id);
        return new DataMerger<>(historyCurrent,historyCommon,historyUpdate).createMergeResult((permission)->true).executeMerge();
    }

    public FactoryUpdateLog<R> revertTo(StoredDataMetadata<S> storedDataMetadata, String user) {
        R historyFactory = getHistoryFactory(storedDataMetadata.id);
        DataAndId<R> currentFactory = dataStorage.getCurrentData();
        return updateCurrentFactory(new DataUpdate<>(
                historyFactory,
                user,
                "revert to: "+storedDataMetadata.id,
                currentFactory.id)
        );
    }

    public FactoryUpdateLog<R> updateCurrentFactory(DataUpdate<R> update) {
        R commonVersion = dataStorage.getHistoryData(update.baseVersionId);
        FactoryUpdateLog<R> factoryLog = factoryManager.update(commonVersion,update.root, update.permissionChecker);
        if (!factoryLog.failedUpdate() && factoryLog.successfullyMerged()){

            S changeSummary=null;
            if (factoryLog.mergeDiffInfo!=null && changeSummaryCreator!=null){
                changeSummary=changeSummaryCreator.createChangeSummary(factoryLog.mergeDiffInfo);
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


    public MergeDiffInfo<R> simulateUpdateCurrentFactory(DataUpdate<R> possibleUpdate){
        R commonVersion = dataStorage.getHistoryData(possibleUpdate.baseVersionId);
        return factoryManager.simulateUpdate(commonVersion , possibleUpdate.root, possibleUpdate.permissionChecker);
    }

    /**
     *  prepare a new factory which could be used to update data. mainly give it the correct baseVersionId
     *  @return new possible factory update with prepared ids/metadata
     * */
    public DataUpdate<R> prepareNewFactory() {
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
    public DataUpdate<R> prepareNewFactory(String user, String comment) {
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

    public Collection<StoredDataMetadata<S>> getHistoryFactoryList() {
        return dataStorage.getHistoryDataList();
    }

    public L start() {
        final DataAndId<R> currentFactory = dataStorage.getCurrentData();
        currentFactory.root.internal().setMicroservice(this);//also mind ExceptionResponseAction#reset
        currentFactory.root.internal().setFactoryTreeBuilder(factoryTreeBuilder);
        return factoryManager.start(new RootFactoryWrapper<>(currentFactory.root));
    }

    public void stop() {
        factoryManager.stop();
    }

    public L getRootLiveObject(){
        return factoryManager.getCurrentFactory().internal().getLiveObject();
    }


}
