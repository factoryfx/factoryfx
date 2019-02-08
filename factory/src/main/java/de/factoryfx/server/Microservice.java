package de.factoryfx.server;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.UUID;

import de.factoryfx.data.merge.DataMerger;
import de.factoryfx.data.merge.MergeDiffInfo;
import de.factoryfx.data.storage.*;
import de.factoryfx.data.storage.migration.GeneralStorageFormat;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.FactoryManager;
import de.factoryfx.factory.RootFactoryWrapper;
import de.factoryfx.factory.log.FactoryUpdateLog;

/**
 * starting point for factoryfx application
 *
 * @param <V> Visitor
 * @param <R> Root
 * @param <S> Summary Data for factory history
 */
public class Microservice<V,L,R extends FactoryBase<L,V,R>,S> {
    private final FactoryManager<V,L,R> factoryManager;
    private final DataStorage<R,S> dataStorage;
    private final ChangeSummaryCreator<R,S> changeSummaryCreator;

    public final GeneralStorageFormat generalStorageFormat;

    public Microservice(FactoryManager<V,L,R> factoryManager, DataStorage<R,S> dataStorage, ChangeSummaryCreator<R,S> changeSummaryCreator, GeneralStorageFormat generalStorageFormat) {
        this.factoryManager = factoryManager;
        this.dataStorage = dataStorage;
        this.changeSummaryCreator = changeSummaryCreator;
        this.generalStorageFormat=generalStorageFormat;
    }

    public MergeDiffInfo<R> getDiffToPreviousVersion(StoredDataMetadata<S> storedDataMetadata) {
        R historyFactory = getHistoryFactory(storedDataMetadata.id);
        R historyFactoryPrevious = getPreviousHistoryFactory(storedDataMetadata.id);
        return new DataMerger<>(historyFactoryPrevious,historyFactoryPrevious.utility().copy(),historyFactory).createMergeResult((permission)->true).executeMerge();
    }

    public FactoryUpdateLog<R> revertTo(StoredDataMetadata<S> storedDataMetadata, String user) {
        R historyFactory = getHistoryFactory(storedDataMetadata.id);
        DataAndStoredMetadata<R,S> current = prepareNewFactory();
        current = new DataAndStoredMetadata<>(historyFactory,current.metadata);
        return updateCurrentFactory(current);
    }

    public FactoryUpdateLog<R> updateCurrentFactory(DataAndStoredMetadata<R,S> update) {
        return updateCurrentFactory(update.metadata.user,update.metadata.comment,update);
    }

    public FactoryUpdateLog<R> updateCurrentFactory(String user, String comment, DataAndStoredMetadata<R,S> update) {
        R commonVersion = dataStorage.getHistoryFactory(update.metadata.baseVersionId);
        FactoryUpdateLog<R> factoryLog = factoryManager.update(commonVersion,update.root, update.permissionChecker);
        if (!factoryLog.failedUpdate() && factoryLog.successfullyMerged()){

            S changeSummary=null;
            if (factoryLog.mergeDiffInfo!=null && changeSummaryCreator!=null){
                changeSummary=changeSummaryCreator.createChangeSummary(factoryLog.mergeDiffInfo);
            }

            R copy = factoryManager.getCurrentFactory().internal().copy();
            StoredDataMetadata<S> copyStoredDataMetadata = new StoredDataMetadata<>(
                    LocalDateTime.now(),
                    update.metadata.id,
                    user,
                    comment,
                    update.metadata.baseVersionId,
                    changeSummary,
                    this.generalStorageFormat,
                    copy.internal().createDataStorageMetadataDictionaryFromRoot()
            );
            dataStorage.updateCurrentFactory(new DataAndStoredMetadata<>(copy,copyStoredDataMetadata));
        }
        return factoryLog;
    }


    public MergeDiffInfo<R> simulateUpdateCurrentFactory(DataAndStoredMetadata<R,S> possibleUpdate){
        R commonVersion = dataStorage.getHistoryFactory(possibleUpdate.metadata.baseVersionId);
        return factoryManager.simulateUpdate(commonVersion , possibleUpdate.root, possibleUpdate.permissionChecker);
    }

    /**
     *  prepare a new factory which could be used to update data. mainly give it the correct baseVersionId
     *  @return new possible factory update with prepared ids/metadata
     * */
    public DataAndStoredMetadata<R,S> prepareNewFactory() {
        DataAndId<R> currentFactory = dataStorage.getCurrentFactory();
        StoredDataMetadata<S> copyMetadata = new StoredDataMetadata<>(
                LocalDateTime.now(),
                UUID.randomUUID().toString(),
                "",
                "",
                currentFactory.id,
                null,
                generalStorageFormat,
                currentFactory.root.internal().createDataStorageMetadataDictionaryFromRoot());
        return new DataAndStoredMetadata<>(currentFactory.root.utility().copy(),copyMetadata);
    }

    public R getHistoryFactory(String id) {
        return dataStorage.getHistoryFactory(id);
    }

    private R getPreviousHistoryFactory(String id) {
        return dataStorage.getPreviousHistoryFactory(id);
    }

    public Collection<StoredDataMetadata<S>> getHistoryFactoryList() {
        return dataStorage.getHistoryFactoryList();
    }

    public L start() {
        final DataAndId<R> currentFactory = dataStorage.getCurrentFactory();

        R copy = currentFactory.root.utility().copy();
        copy.internalFactory().setMicroservice(this);//also mind ExceptionResponseAction#reset
        return factoryManager.start(new RootFactoryWrapper<>(copy));
    }

    public void stop() {
        factoryManager.stop();
    }

    public V query(V visitor) {
        return factoryManager.query(visitor);
    }

    public L getRootLiveObject(){
        return factoryManager.getCurrentFactory().internalFactory().getLiveObject();
    }
}
