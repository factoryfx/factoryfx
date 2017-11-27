package de.factoryfx.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import de.factoryfx.data.merge.AttributeDiffInfo;
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
 * @param <L> Root live object
 * @param <R> Root
 */
public class ApplicationServer<V,L,R extends FactoryBase<L,V>> {
    private final FactoryManager<V,L,R> factoryManager;
    private final DataStorage<R> dataStorage;

    public ApplicationServer(FactoryManager<V,L,R> factoryManager, DataStorage<R> dataStorage) {
        this.factoryManager = factoryManager;
        this.dataStorage = dataStorage;
    }

    public MergeDiffInfo<R> getDiffToPreviousVersion(StoredDataMetadata storedDataMetadata) {
        R historyFactory = getHistoryFactory(storedDataMetadata.id);
        R historyFactoryPrevious = getPreviousHistoryFactory(storedDataMetadata.id);
        return new DataMerger<>(historyFactoryPrevious,historyFactoryPrevious,historyFactory).createMergeResult((permission)->true).executeMerge();
    }

    public FactoryUpdateLog revertTo(StoredDataMetadata storedDataMetadata, String user) {
        R historyFactory = getHistoryFactory(storedDataMetadata.id);
        DataAndNewMetadata<R> current = prepareNewFactory();
        current = new DataAndNewMetadata<>(historyFactory,current.metadata);
        return updateCurrentFactory(current,user,"revert",s->true);
    }

    /**
     * @param factoryId the id
     * @return list all changes made in specific factory
     */
    public List<AttributeDiffInfo> getDiffHistoryForFactory(String factoryId) {
        final ArrayList<AttributeDiffInfo> result = new ArrayList<>();
//        final List<StoredFactoryMetadata> historyFactoryList = new ArrayList<>(factoryStorage.getHistoryFactoryList()).stream().sorted(Comparator.comparing(o -> o.creationTime)).collect(Collectors.toList());
//        Collections.reverse(historyFactoryList);
//        for (int i=0;i<historyFactoryList.size()-1;i++){
//            R historyFactory = getHistoryFactory(historyFactoryList.get(i).id);
//            R historyFactoryPrevious = getHistoryFactory(historyFactoryList.get(i+1).id);
//            final MergeDiffInfo mergeResult = new DataMerger(historyFactoryPrevious, historyFactoryPrevious, historyFactory).createMergeResult((permission) -> true).getMergeDiff();
//            mergeResult.mergeInfos.forEach(attributeDiffInfo -> {
//                if (attributeDiffInfo.isFromFactory(factoryId)) {
//                    result.add(attributeDiffInfo);
//                }
//            });
//        }
        return result;
    }

    public FactoryUpdateLog updateCurrentFactory(DataAndNewMetadata<R> update, String user, String comment, Function<String,Boolean> permissionChecker) {
        prepareFactory(update.root);
        R commonVersion = dataStorage.getHistoryFactory(update.metadata.baseVersionId);
        FactoryUpdateLog factoryLog = factoryManager.update(commonVersion, update.root, permissionChecker);
        if (factoryLog.mergeDiffInfo.successfullyMerged()){
            DataAndNewMetadata<R> copy = new DataAndNewMetadata<>(factoryManager.getCurrentFactory().internal().copyFromRoot(),update.metadata);
            dataStorage.updateCurrentFactory(copy,user,comment);
        }
        return factoryLog;
    }

    public MergeDiffInfo simulateUpdateCurrentFactory(DataAndNewMetadata<R> possibleUpdate, Function<String, Boolean> permissionChecker){
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

    public Collection<StoredDataMetadata> getHistoryFactoryList() {
        return dataStorage.getHistoryFactoryList();
    }

    public void start() {
        dataStorage.loadInitialFactory();
        final DataAndStoredMetadata<R> currentFactory = dataStorage.getCurrentFactory();
        prepareFactory(currentFactory.root);
        factoryManager.start(currentFactory.root);
    }

    @SuppressWarnings("unchecked")
    void prepareFactory(R root){
        root.internal().collectChildrenDeep().forEach(data -> {
            if (data instanceof ApplicationServerAwareFactory){
                ((ApplicationServerAwareFactory<V,L, R,?>)data).applicationServer.set(this);
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
