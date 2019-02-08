package de.factoryfx.data.storage;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import de.factoryfx.data.Data;

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
     * @return the added factory metadata
     */
    default void addFutureFactory(DataAndScheduledMetadata<R,S> futureFactory) {
        throw new UnsupportedOperationException();
    }

    /**
     * get the current factory, if first start or no available an initial factory is created
     * @return current factory
     * */
    DataAndId<R> getCurrentFactory();

    /**
     * updateCurrentFactory and history
     * @param update update
     */
    void updateCurrentFactory(DataAndStoredMetadata<R,S> update);

}
