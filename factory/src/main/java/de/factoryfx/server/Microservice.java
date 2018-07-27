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

    public Microservice(FactoryManager<V,L,R> factoryManager, DataStorage<R,S> dataStorage) {
        this.factoryManager = factoryManager;
        this.dataStorage = dataStorage;
    }

    public MergeDiffInfo<R> getDiffToPreviousVersion(StoredDataMetadata storedDataMetadata) {
        R historyFactory = getHistoryFactory(storedDataMetadata.id);
        R historyFactoryPrevious = getPreviousHistoryFactory(storedDataMetadata.id);
        return new DataMerger<>(historyFactoryPrevious,historyFactoryPrevious.utility().copy(),historyFactory).createMergeResult((permission)->true).executeMerge();
    }

    public FactoryUpdateLog<R> revertTo(StoredDataMetadata storedDataMetadata, String user) {
        R historyFactory = getHistoryFactory(storedDataMetadata.id);
        DataAndNewMetadata<R> current = prepareNewFactory();
        current = new DataAndNewMetadata<>(historyFactory,current.metadata);
        return updateCurrentFactory(current,user,"revert",s->true);
    }

    public FactoryUpdateLog<R> updateCurrentFactory(DataAndNewMetadata<R> update, String user, String comment, Function<String,Boolean> permissionChecker) {
        R commonVersion = dataStorage.getHistoryFactory(update.metadata.baseVersionId);
        FactoryUpdateLog<R> factoryLog = factoryManager.update(commonVersion,update.root, permissionChecker);
        if (!factoryLog.failedUpdate()&& factoryLog.successfullyMerged()){
            DataAndNewMetadata<R> copy = new DataAndNewMetadata<>(factoryManager.getCurrentFactory().internal().copy(),update.metadata);
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
        return dataStorage.prepareNewFactory(dataStorage.getCurrentFactoryStorageId(),factoryManager.getCurrentFactory().utility().copy());
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
        dataStorage.loadInitialFactory();
        final DataAndStoredMetadata<R,S> currentFactory = dataStorage.getCurrentFactory();
        currentFactory.root.internalFactory().setMicroservice(this);//also mind ExceptionResponseAction#reset

        return factoryManager.start(new RootFactoryWrapper<>(currentFactory.root));
    }

    public void stop() {
        factoryManager.stop();
    }

    public V query(V visitor) {
        return factoryManager.query(visitor);
    }
}
