package de.factoryfx.data.storage.inmemory;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import de.factoryfx.data.Data;
import de.factoryfx.data.storage.*;


/**
 *
 * @param <R> Root factory
 * @param <S> Storage history summary
 */
public class InMemoryDataStorage<R extends Data,S> implements DataStorage<R,S> {
    private final Map<String,DataAndStoredMetadata<R,S>> storage = new TreeMap<>();
    private final Map<String, ScheduledUpdate<R>> future = new TreeMap<>();
    private String currentFactoryId;

    public InMemoryDataStorage(R initialFactory){
        initialFactory.internal().addBackReferences();
        this.currentFactoryId=UUID.randomUUID().toString();

        StoredDataMetadata<S> metadata = new StoredDataMetadata<>(currentFactoryId, "System", "initial", currentFactoryId,null,null,null);
        storage.put(currentFactoryId,new DataAndStoredMetadata<>(initialFactory, metadata));
    }

    @Override
    public R getHistoryFactory(String id) {
        DataAndStoredMetadata<R,S> data = storage.get(id);
        return data.root.utility().copy();
    }

    @Override
    public Collection<StoredDataMetadata<S>> getHistoryFactoryList() {
        return storage.values().stream().map(item -> item.metadata).collect(Collectors.toList());
    }

    @Override
    public DataAndId<R> getCurrentFactory() {
        return new DataAndId<>(storage.get(currentFactoryId).root.internal().copy(),currentFactoryId);
    }

    @Override
    public void updateCurrentFactory(DataUpdate<R> update, S changeSummary) {
        StoredDataMetadata<S> metadata = new StoredDataMetadata<>(LocalDateTime.now(),
                UUID.randomUUID().toString(),
                update.user,
                update.comment,
                update.baseVersionId,
                changeSummary, null,
                update.root.internal().createDataStorageMetadataDictionaryFromRoot()
        );

        storage.put(metadata.id, new DataAndStoredMetadata<>(update.root,metadata));
        currentFactoryId=metadata.id;
    }

    @Override
    public Collection<ScheduledUpdateMetadata> getFutureFactoryList() {
        ArrayList<ScheduledUpdateMetadata> result = new ArrayList<>();
        for (Map.Entry<String, ScheduledUpdate<R>> entry : future.entrySet()) {
            result.add(new ScheduledUpdateMetadata(
                    entry.getKey(),
                    entry.getValue().user,
                    entry.getValue().comment,
                    entry.getValue().scheduled,null,null
            ));
        }
        return result;
    }

    @Override
    public void deleteFutureFactory(String id) {
        future.remove(id);
    }

    public R getFutureFactory(String id) {
        ScheduledUpdate<R> data = future.get(id);
        return data.root.internal().copy();
    }

    @Override
    public void addFutureFactory(ScheduledUpdate<R> futureFactory) {
        future.put(UUID.randomUUID().toString(), futureFactory);
    }

}
