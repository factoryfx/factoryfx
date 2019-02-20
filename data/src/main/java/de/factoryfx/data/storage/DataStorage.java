package de.factoryfx.data.storage;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import de.factoryfx.data.Data;

/**
 * storage/load and history for factories
 *
 * @param <R> Root
 * @param <S> Change summary
 */
public interface DataStorage<R extends Data, S> {

    R getHistoryData(String id);

    default R getPreviousHistoryData(String id) {
        Collection<StoredDataMetadata<S>> historyDataList = getHistoryDataList();
        if (historyDataList.isEmpty())
            return null;
        List<StoredDataMetadata> historyDataListSorted = historyDataList.stream().sorted(Comparator.comparing(h -> h.creationTime)).collect(Collectors.toList());
        for (int i=0;i<historyDataListSorted.size();i++) {
            if (historyDataListSorted.get(i).id.equals(id) && i-1>=0) {
                return getHistoryData(historyDataListSorted.get(i - 1).id);
            }
        }
        return null;
    }

    Collection<StoredDataMetadata<S>> getHistoryDataList();

    Collection<ScheduledUpdateMetadata> getFutureDataList();

    void deleteFutureData(String id);

    R getFutureData(String id);

    /**
     * @param futureData futureData
     */
    void addFutureData(ScheduledUpdate<R> futureData);

    /**
     * get the current data, if first start or no available an initial data is created
     * @return current data
     * */
    DataAndId<R> getCurrentData();

    /**
     * updateCurrentData and history
     * @param update update
     * @param changeSummary changeSummary
     */
    void updateCurrentData(DataUpdate<R> update, S changeSummary);

    /**
     * for one-time migration
     * apply patch to all stored data including history, changes to jsonNodes are stored
     * @param consumer called for all stored factories
     */
    void patchAll(DataStoragePatcher consumer);

    /**
     * for one-time migration
     * apply patch to current data
     * @param consumer called for current factory, changes to jsonNodes are stored
     */
    void patchCurrentData(DataStoragePatcher consumer);


}
