package io.github.factoryfx.factory.storage;

import io.github.factoryfx.factory.FactoryBase;

import java.util.*;


/**
 * storage/load and history for factories
 *
 * @param <R> Root
 */
public interface DataStorage<R extends FactoryBase<?,?>> {

    R getHistoryData(String id);

    Collection<StoredDataMetadata> getHistoryDataList();

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
     * get the current data, if first start or no available an initial data is created
     * @return current data
     * */
    default R getInitialData(){
        List<StoredDataMetadata> historyDataList = new ArrayList<>(getHistoryDataList());
        historyDataList.sort(Comparator.comparing(o -> o.creationTime));
        return getHistoryData(historyDataList.get(0).id);
    }

    /**
     * updateCurrentData and history
     * @param update updata data
     * @param updateSummary update description
     */
    void updateCurrentData(DataUpdate<R> update, UpdateSummary updateSummary);

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
