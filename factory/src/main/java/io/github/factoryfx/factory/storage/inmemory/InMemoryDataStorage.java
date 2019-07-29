package io.github.factoryfx.factory.storage.inmemory;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.storage.*;


/**
 * InMemoryDataStorage stores factories in the ram which means that changes are lost after a restart.
 *
 * @param <R> Root factory
 */
public class InMemoryDataStorage<R extends FactoryBase<?,?>> implements DataStorage<R> {
    private final Map<String, DataAndStoredMetadata<R>> storage = new TreeMap<>();
    private final Map<String, ScheduledUpdate<R>> future = new TreeMap<>();
    private String currentFactoryId;

    public InMemoryDataStorage(R initialFactory){
        initialFactory.internal().finalise();
        this.currentFactoryId=UUID.randomUUID().toString();

        StoredDataMetadata metadata = new StoredDataMetadata(currentFactoryId, "System", "initial", currentFactoryId,null,null,null);
        storage.put(currentFactoryId,new DataAndStoredMetadata<>(initialFactory, metadata));
    }

    @Override
    public R getHistoryData(String id) {
        DataAndStoredMetadata<R> data = storage.get(id);
        return data.root.utility().copy();
    }

    @Override
    public Collection<StoredDataMetadata> getHistoryDataList() {
        return storage.values().stream().map(item -> item.metadata).collect(Collectors.toList());
    }

    @Override
    public DataAndId<R> getCurrentData() {
        return new DataAndId<>(storage.get(currentFactoryId).root.utility().copy(),currentFactoryId);
    }

    @Override
    public void updateCurrentData(DataUpdate<R> update, UpdateSummary changeSummary) {
        StoredDataMetadata metadata = new StoredDataMetadata(LocalDateTime.now(),
                UUID.randomUUID().toString(),
                update.user,
                update.comment,
                update.baseVersionId,
                changeSummary,
                update.root.internal().createDataStorageMetadataDictionaryFromRoot(),currentFactoryId
        );

        storage.put(metadata.id, new DataAndStoredMetadata<>(update.root,metadata));
        currentFactoryId=metadata.id;
    }

    @Override
    public void patchAll(DataStoragePatcher consumer) {
        throw new UnsupportedOperationException("in memory format can't be outdated");
    }

    @Override
    public void patchCurrentData(DataStoragePatcher consumer) {
        throw new UnsupportedOperationException("in memory format can't be outdated");
    }

    @Override
    public Collection<ScheduledUpdateMetadata> getFutureDataList() {
        ArrayList<ScheduledUpdateMetadata> result = new ArrayList<>();
        for (Map.Entry<String, ScheduledUpdate<R>> entry : future.entrySet()) {
            result.add(new ScheduledUpdateMetadata(
                    entry.getKey(),
                    entry.getValue().user,
                    entry.getValue().comment,
                    entry.getValue().scheduled,null
            ));
        }
        return result;
    }

    @Override
    public void deleteFutureData(String id) {
        future.remove(id);
    }

    public R getFutureData(String id) {
        ScheduledUpdate<R> data = future.get(id);
        return data.root.utility().copy();
    }

    @Override
    public void addFutureData(ScheduledUpdate<R> futureFactory) {
        future.put(UUID.randomUUID().toString(), futureFactory);
    }

}
