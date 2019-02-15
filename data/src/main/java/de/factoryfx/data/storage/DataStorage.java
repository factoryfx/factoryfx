package de.factoryfx.data.storage;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;
import de.factoryfx.data.Data;

/**
 * storage/load and history for factories
 *
 * @param <R> Root
 * @param <S> Change summary
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

    Collection<ScheduledUpdateMetadata> getFutureFactoryList();

    void deleteFutureFactory(String id);

    R getFutureFactory(String id);

    /**
     * @param futureFactory futureFactory
     * @return the added factory metadata
     */
    void addFutureFactory(ScheduledUpdate<R> futureFactory);

    /**
     * get the current factory, if first start or no available an initial factory is created
     * @return current factory
     * */
    DataAndId<R> getCurrentFactory();

    /**
     * updateCurrentFactory and history
     * @param update update
     */
    void updateCurrentFactory(DataUpdate<R> update, S changeSummary);

}
