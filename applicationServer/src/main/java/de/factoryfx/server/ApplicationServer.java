package de.factoryfx.server;

import java.util.Collection;
import java.util.function.Function;

import de.factoryfx.data.merge.DataMerger;
import de.factoryfx.data.merge.MergeDiffInfo;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.FactoryManager;
import de.factoryfx.factory.datastorage.FactoryAndNewMetadata;
import de.factoryfx.factory.datastorage.FactoryAndStoredMetadata;
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

    public FactoryUpdateLog revertTo(StoredFactoryMetadata storedFactoryMetadata, String user) {
        T historyFactory = getHistoryFactory(storedFactoryMetadata.id);
        FactoryAndNewMetadata<T> current = prepareNewFactory();
        current = new FactoryAndNewMetadata<>(historyFactory,current.metadata);
        return updateCurrentFactory(current,user,"revert",s->true);
    }

    public FactoryUpdateLog updateCurrentFactory(FactoryAndNewMetadata<T> update, String user, String comment, Function<String,Boolean> permissionChecker) {
        prepareFactory(update.root);
        T commonVersion = factoryStorage.getHistoryFactory(update.metadata.baseVersionId);
        FactoryUpdateLog factoryLog = factoryManager.update(commonVersion, update.root, permissionChecker);
        if (factoryLog.mergeDiffInfo.successfullyMerged()){
            FactoryAndNewMetadata<T> copy = new FactoryAndNewMetadata<>(factoryManager.getCurrentFactory().internal().copy(),update.metadata);
            factoryStorage.updateCurrentFactory(copy,user,comment);
        }
        return factoryLog;
    }

    public MergeDiffInfo simulateUpdateCurrentFactory(FactoryAndNewMetadata<T> possibleUpdate, Function<String, Boolean> permissionChecker){
        T commonVersion = factoryStorage.getHistoryFactory(possibleUpdate.metadata.baseVersionId);
        return factoryManager.simulateUpdate(commonVersion , possibleUpdate.root, permissionChecker);
    }

    /** creates a new factory update which is ready for editing mainly assign the right ids*/
    public FactoryAndNewMetadata<T> prepareNewFactory() {
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
        final FactoryAndStoredMetadata<T> currentFactory = factoryStorage.getCurrentFactory();
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
