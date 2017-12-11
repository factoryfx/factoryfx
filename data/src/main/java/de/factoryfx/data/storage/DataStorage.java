package de.factoryfx.data.storage;

import java.time.LocalDateTime;
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
 */
public interface DataStorage<R extends Data> {

    R getHistoryFactory(String id);

    default R getPreviousHistoryFactory(String id) {
        Collection<StoredDataMetadata> historyFactoryList = getHistoryFactoryList();
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

    Collection<StoredDataMetadata> getHistoryFactoryList();

    default Collection<ScheduledDataMetadata> getFutureFactoryList() {
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
     * @return the added factory metadata
     */
    default ScheduledDataMetadata addFutureFactory(R futureFactory, NewScheduledDataMetadata futureFactoryMetadata, String user, String comment) {
        throw new UnsupportedOperationException();
    }


    DataAndStoredMetadata<R> getCurrentFactory();

    /**
     * prepare a new Factory which could we an update. mainly give it the correct baseVersionId
     * @return new possible factory update with prepared ids
     * */
    DataAndNewMetadata<R> getPrepareNewFactory();

    /**
     * updateCurrentFactory and history
     * @param update update
     * @param user user
     * @param comment comment
     */
    void  updateCurrentFactory(DataAndNewMetadata<R> update, String user, String comment);

    /**
     * at Application start load current Factory
     * */
    void loadInitialFactory();
}
