package de.factoryfx.server;

import java.util.Collection;
import java.util.function.Function;

import de.factoryfx.data.merge.DataMerger;
import de.factoryfx.data.merge.MergeDiffInfo;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.FactoryManager;
import de.factoryfx.data.storage.DataAndNewMetadata;
import de.factoryfx.data.storage.DataAndStoredMetadata;
import de.factoryfx.data.storage.DataStorage;
import de.factoryfx.data.storage.StoredDataMetadata;
import de.factoryfx.factory.log.FactoryUpdateLog;

/**
 * starting point for factoryfx application
 *
 * @param <V> Visitor
 * @param <R> Root
 * @param <S> Summary Data for factory history
 */
public class ApplicationServer<V,R extends FactoryBase<?,V,R>,S> {
    private final FactoryManager<V,R> factoryManager;
    private final DataStorage<R,S> dataStorage;

    public ApplicationServer(FactoryManager<V,R> factoryManager, DataStorage<R,S> dataStorage) {
        this.factoryManager = factoryManager;
        this.dataStorage = dataStorage;
    }

    public MergeDiffInfo<R> getDiffToPreviousVersion(StoredDataMetadata storedDataMetadata) {
        R historyFactory = getHistoryFactory(storedDataMetadata.id);
        R historyFactoryPrevious = getPreviousHistoryFactory(storedDataMetadata.id);
        return new DataMerger<>(historyFactoryPrevious,historyFactoryPrevious,historyFactory).createMergeResult((permission)->true).executeMerge();
    }

    public FactoryUpdateLog<R> revertTo(StoredDataMetadata storedDataMetadata, String user) {
        R historyFactory = getHistoryFactory(storedDataMetadata.id);
        DataAndNewMetadata<R> current = prepareNewFactory();
        current = new DataAndNewMetadata<>(historyFactory,current.metadata);
        return updateCurrentFactory(current,user,"revert",s->true);
    }

    public FactoryUpdateLog<R> updateCurrentFactory(DataAndNewMetadata<R> update, String user, String comment, Function<String,Boolean> permissionChecker) {
        R commonVersion = dataStorage.getHistoryFactory(update.metadata.baseVersionId);
        FactoryUpdateLog<R> factoryLog = factoryManager.update(commonVersion, update.root, permissionChecker);
        if (factoryLog.mergeDiffInfo.successfullyMerged()){
            DataAndNewMetadata<R> copy = new DataAndNewMetadata<>(factoryManager.getCurrentFactory().internal().copyFromRoot(),update.metadata);
            dataStorage.updateCurrentFactory(copy,user,comment,factoryLog.mergeDiffInfo);
        }
        return factoryLog;
    }

    public MergeDiffInfo<R> simulateUpdateCurrentFactory(DataAndNewMetadata<R> possibleUpdate, Function<String, Boolean> permissionChecker){
        R commonVersion = dataStorage.getHistoryFactory(possibleUpdate.metadata.baseVersionId);
        return factoryManager.simulateUpdate(commonVersion , possibleUpdate.root, permissionChecker);
    }

    /**
     * @return creates a new factory update which is ready for editing mainly assign the right ids
     * */
    public DataAndNewMetadata<R> prepareNewFactory() {
        return dataStorage.getPrepareNewFactory();
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

    public void start() {
        dataStorage.loadInitialFactory();
        final DataAndStoredMetadata<R,S> currentFactory = dataStorage.getCurrentFactory();
        currentFactory.root.internalFactory().setApplicationServer(this);

        factoryManager.start(currentFactory.root);
    }

    public void stop() {
        factoryManager.stop();
    }

    public V query(V visitor) {
        return factoryManager.query(visitor);
    }
}
