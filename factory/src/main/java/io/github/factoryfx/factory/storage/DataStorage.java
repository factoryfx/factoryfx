package io.github.factoryfx.factory.storage;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.storage.migration.ConfigurationPatch;

import java.util.*;


/**
 * storage/load and history for factories
 *
 * @param <R> Root
 */
public interface DataStorage<R extends FactoryBase<?,?>> {

    R getHistoryData(String id);

    Collection<StoredDataMetadata> getHistoryDataList(boolean light);

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
     * get the current data id
     * */
    String getCurrentDataId();

    /**
     * get the current data as stored (raw json, no patches or migrations applied), e.g. for a configuration snapshot
     * @return raw current data and metadata
     * */
    RawFactoryDataAndMetadata getCurrentDataRaw();

    /**
     * store a raw configuration (json and complete metadata) as the new current configuration and add it to the history,
     * exactly as given: no patches or migrations are applied and the metadata (including configurationSchemaVersion and
     * dataStorageMetadataDictionary) is persisted unchanged. counterpart of {@link #getCurrentDataRaw()}, e.g. for
     * restoring a configuration snapshot without baking the registered patches into the stored form
     * @param rawDataAndMetadata raw json and complete metadata to store
     */
    void updateCurrentDataRaw(RawFactoryDataAndMetadata rawDataAndMetadata);

    /**
     * get the initial data created from a FactoryTreeBuilder
     * @return initial data
     * */
    default R getInitialData(){
        for (StoredDataMetadata metadata : getHistoryDataList(true)) {
            if (metadata.isInitialFactory()){
                return getHistoryData(metadata.id);
            }
        }
        return  null;
    }

    /**
     * updateCurrentData and history<br>
     * implementations must not retain a reference to update.root after this method returns
     * (the caller may pass the live factory tree and mutate it afterwards); implementations
     * that store the object itself instead of a serialized form must copy it
     * @param update updata data
     * @param updateSummary update description
     */
    void updateCurrentData(DataUpdate<R> update, UpdateSummary updateSummary);

    /**
     * apply a patch to all stored configurations including history, the patched json and metadata are written back to the storage.<br>
     * persistence primitive for {@link io.github.factoryfx.server.MicroserviceDeployment#persistConfigurationPatches()} &ndash; projects
     * should register their patches on the {@link io.github.factoryfx.factory.builder.MicroserviceBuilder} (withPatch) and persist
     * them via persistConfigurationPatches instead of calling this directly
     * @param consumer called for all stored factories
     */
    void patchAll(ConfigurationPatch consumer);

}
