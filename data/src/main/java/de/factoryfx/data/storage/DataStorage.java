package de.factoryfx.data.storage;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import de.factoryfx.data.Data;
import de.factoryfx.data.merge.MergeDiffInfo;

/**
 * storage/load and history for factories
 *
 * @param <R> Root
 * @param <S> Change Summary
 */
public interface DataStorage<R extends Data, S> {

    R getHistoryFactory(String id);

    default R getPreviousHistoryFactory(String id) {
        Collection<StoredDataMetadata<S>> historyFactoryList = getHistoryFactoryList();
        if (historyFactoryList.isEmpty())
            return null;
        List<StoredDataMetadata> historyFactoryListSorted = historyFactoryList.stream().sorted(Comparator.comparing(h -> h.creationTime)).collect(Collectors.toList());
        for (int i=0;i<historyFactoryListSorted.size();i++) {
            if (historyFactoryListSorted.get(i).id.equals(id) && i-1>=0) {
                return getHistoryFactory(historyFactoryListSorted.get(i - 1).id);
            }
        }
        return null;
    }

    Collection<StoredDataMetadata<S>> getHistoryFactoryList();

    default Collection<ScheduledDataMetadata<S>> getFutureFactoryList() {
        return Collections.emptyList();
    }

    default void deleteFutureFactory(String id) {
        throw new UnsupportedOperationException();
    }

    default R getFutureFactory(String id) {
        throw new UnsupportedOperationException();
    }

    /**
     * @param futureFactory futureFactory
     * @param futureFactoryMetadata futureFactoryMetadata
     * @param user user
     * @param comment comment
     * @param mergeDiff mergeDiff
     * @return the added factory metadata
     */
    default ScheduledDataMetadata<S> addFutureFactory(R futureFactory, NewScheduledDataMetadata futureFactoryMetadata, String user, String comment, MergeDiffInfo<R> mergeDiff) {
        throw new UnsupportedOperationException();
    }


    DataAndStoredMetadata<R,S> getCurrentFactory();

    String getCurrentFactoryStorageId();


    DataAndNewMetadata<R> prepareNewFactory(String currentFactoryStorageId, R currentFactoryCopy);

    /**
     * prepare a new factory which could be used to update data. mainly give it the correct baseVersionId
     * @return new possible factory update with prepared ids/metadata
     * */
    default DataAndNewMetadata<R> prepareNewFactory(){
        return prepareNewFactory(getCurrentFactoryStorageId(),getCurrentFactory().root.utility().copy());
    }

    /**
     * updateCurrentFactory and history
     * @param update update
     * @param user user
     * @param comment comment
     * @param mergeDiff mergeDiff
     */
    void  updateCurrentFactory(DataAndNewMetadata<R> update, String user, String comment, MergeDiffInfo<R> mergeDiff);

    /**
     * at Application start load current Factory
     * */
    void loadInitialFactory();
}
