package de.factoryfx.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import de.factoryfx.data.merge.AttributeDiffInfo;
import de.factoryfx.data.merge.DataMerger;
import de.factoryfx.data.merge.MergeDiffInfo;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.FactoryManager;
import de.factoryfx.factory.datastorage.FactoryAndNewMetadata;
import de.factoryfx.factory.datastorage.FactoryAndStoredMetadata;
import de.factoryfx.factory.datastorage.FactoryStorage;
import de.factoryfx.factory.datastorage.StoredFactoryMetadata;
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
    private final FactoryStorage<V,L,R> factoryStorage;

    public ApplicationServer(FactoryManager<V,L,R> factoryManager, FactoryStorage<V,L,R> factoryStorage) {
        this.factoryManager = factoryManager;
        this.factoryStorage = factoryStorage;
    }

    public MergeDiffInfo getDiffToPreviousVersion(StoredFactoryMetadata storedFactoryMetadata) {
        R historyFactory = getHistoryFactory(storedFactoryMetadata.id);
        R historyFactoryPrevious = getPreviousHistoryFactory(storedFactoryMetadata.id);
        return new DataMerger(historyFactoryPrevious,historyFactoryPrevious,historyFactory).createMergeResult((permission)->true).executeMerge();
    }

    public FactoryUpdateLog revertTo(StoredFactoryMetadata storedFactoryMetadata, String user) {
        R historyFactory = getHistoryFactory(storedFactoryMetadata.id);
        FactoryAndNewMetadata<R> current = prepareNewFactory();
        current = new FactoryAndNewMetadata<>(historyFactory,current.metadata);
        return updateCurrentFactory(current,user,"revert",s->true);
    }

    /**list all changes made in specific factory*/
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

    public FactoryUpdateLog updateCurrentFactory(FactoryAndNewMetadata<R> update, String user, String comment, Function<String,Boolean> permissionChecker) {
        prepareFactory(update.root);
        R commonVersion = factoryStorage.getHistoryFactory(update.metadata.baseVersionId);
        FactoryUpdateLog factoryLog = factoryManager.update(commonVersion, update.root, permissionChecker);
        if (factoryLog.mergeDiffInfo.successfullyMerged()){
            FactoryAndNewMetadata<R> copy = new FactoryAndNewMetadata<>(factoryManager.getCurrentFactory().internal().copy(),update.metadata);
            factoryStorage.updateCurrentFactory(copy,user,comment);
        }
        return factoryLog;
    }

    public MergeDiffInfo simulateUpdateCurrentFactory(FactoryAndNewMetadata<R> possibleUpdate, Function<String, Boolean> permissionChecker){
        R commonVersion = factoryStorage.getHistoryFactory(possibleUpdate.metadata.baseVersionId);
        return factoryManager.simulateUpdate(commonVersion , possibleUpdate.root, permissionChecker);
    }

    /** creates a new factory update which is ready for editing mainly assign the right ids*/
    public FactoryAndNewMetadata<R> prepareNewFactory() {
        return factoryStorage.getPrepareNewFactory();
    }

    public R getHistoryFactory(String id) {
        return factoryStorage.getHistoryFactory(id);
    }

    private R getPreviousHistoryFactory(String id) {
        return factoryStorage.getPreviousHistoryFactory(id);
    }

    public Collection<StoredFactoryMetadata> getHistoryFactoryList() {
        return factoryStorage.getHistoryFactoryList();
    }

    public void start() {
        factoryStorage.loadInitialFactory();
        final FactoryAndStoredMetadata<R> currentFactory = factoryStorage.getCurrentFactory();
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
