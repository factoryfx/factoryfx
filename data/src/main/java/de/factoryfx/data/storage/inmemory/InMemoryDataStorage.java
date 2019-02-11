package de.factoryfx.data.storage.inmemory;

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
    private final Map<String,DataAndScheduledMetadata<R,S>> future = new TreeMap<>();
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
    public void updateCurrentFactory(DataAndStoredMetadata<R,S> update) {
        storage.put(update.metadata.id, update);
        currentFactoryId=update.metadata.id;
    }

    @Override
    public Collection<ScheduledDataMetadata<S>> getFutureFactoryList() {
        return future.values().stream().map(item -> item.metadata).collect(Collectors.toList());
    }

    @Override
    public void deleteFutureFactory(String id) {
        future.remove(id);
    }

    public R getFutureFactory(String id) {
        DataAndScheduledMetadata<R,S> data = future.get(id);
        return data.root.internal().copy();
    }

    @Override
    public void addFutureFactory(DataAndScheduledMetadata<R,S> futureFactory) {
        future.put(futureFactory.metadata.id, futureFactory);
    }

}
