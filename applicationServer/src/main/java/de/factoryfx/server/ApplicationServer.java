package de.factoryfx.server;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.function.Function;

import de.factoryfx.data.merge.DataMerger;
import de.factoryfx.data.merge.MergeDiffInfo;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.FactoryManager;
import de.factoryfx.factory.datastorage.FactoryAndStorageMetadata;
import de.factoryfx.factory.datastorage.FactoryStorage;
import de.factoryfx.factory.datastorage.StoredFactoryMetadata;
import de.factoryfx.factory.log.FactoryUpdateLog;

public class ApplicationServer<L,V,T extends FactoryBase<L,V>> {
    private final FactoryManager<L,V,T> factoryManager;
    private final FactoryStorage<L,V,T> factoryStorage;

    public ApplicationServer(FactoryManager<L,V,T> factoryManager, FactoryStorage<L,V,T> factoryStorage) {
        this.factoryManager = factoryManager;
        this.factoryStorage = factoryStorage;
    }

    public MergeDiffInfo getDiffToPreviousVersion(StoredFactoryMetadata storedFactoryMetadata) {
        T historyFactory = getHistoryFactory(storedFactoryMetadata.id);
        T historyFactoryPrevious = getPreviousHistoryFactory(storedFactoryMetadata.id);
        return new DataMerger(historyFactoryPrevious,historyFactoryPrevious,historyFactory).createMergeResult((permission)->true);
    }

    public FactoryUpdateLog updateCurrentFactory(FactoryAndStorageMetadata<T> update, Function<String,Boolean> permissionChecker) {
        prepareFactory(update.root);
        T commonVersion = factoryStorage.getHistoryFactory(update.metadata.baseVersionId);
        FactoryUpdateLog factoryLog = factoryManager.update(commonVersion, update.root, permissionChecker);
        if (factoryLog.mergeDiffInfo.successfullyMerged()){
            update.metadata.creationTime= LocalDateTime.now();
            FactoryAndStorageMetadata<T> copy = new FactoryAndStorageMetadata<>(factoryManager.getCurrentFactory().internal().copy(),update.metadata);
            factoryStorage.updateCurrentFactory(copy);
        }
        return factoryLog;
    }

    public MergeDiffInfo simulateUpdateCurrentFactory(FactoryAndStorageMetadata<T> possibleUpdate){
        T commonVersion = factoryStorage.getHistoryFactory(possibleUpdate.metadata.baseVersionId);
        return factoryManager.simulateUpdate(commonVersion , possibleUpdate.root);
    }

    /** creates a new factory update which is ready for editing mainly assign the right ids*/
    public FactoryAndStorageMetadata<T> prepareNewFactory() {
        return factoryStorage.getPrepareNewFactory();
    }

    public T getHistoryFactory(String id) {
        return factoryStorage.getHistoryFactory(id);
    }

    public T getPreviousHistoryFactory(String id) {
        T historyFactory = factoryStorage.getPreviousHistoryFactory(id);
        return historyFactory;
    }

    public Collection<StoredFactoryMetadata> getHistoryFactoryList() {
        return factoryStorage.getHistoryFactoryList();
    }

    public void start() {
        factoryStorage.loadInitialFactory();
        final FactoryAndStorageMetadata<T> currentFactory = factoryStorage.getCurrentFactory();
        prepareFactory(currentFactory.root);
        factoryManager.start(currentFactory.root);
    }

    @SuppressWarnings("unchecked")
    void prepareFactory(T root){
        root.internal().collectChildrenDeep().forEach(data -> {
            if (data instanceof ApplicationServerAwareFactory){
                ((ApplicationServerAwareFactory<V,L,T,?>)data).applicationServer.set(this);
            }
        });
    }

    public void stop() {
        factoryManager.stop();
    }

    public V query(V visitor) {
        return factoryManager.query(visitor);
    }
}
