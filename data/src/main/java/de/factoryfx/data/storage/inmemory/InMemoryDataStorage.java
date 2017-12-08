package de.factoryfx.data.storage.inmemory;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import de.factoryfx.data.Data;
import de.factoryfx.data.storage.*;


public class InMemoryDataStorage<R extends Data> implements DataStorage<R> {
    private Map<String,DataAndStoredMetadata<R>> storage = new TreeMap<>();
    private Map<String,DataAndScheduledMetadata<R>> future = new TreeMap<>();
    private String current;
    private R initialFactory;

    public InMemoryDataStorage(R initialFactory){
        initialFactory.internal().checkUsable();
        this.initialFactory=initialFactory;
    }



    @Override
    public R getHistoryFactory(String id) {
        DataAndStoredMetadata<R> data = storage.get(id);
        return data.root.internal().copyFromRoot();
    }

    @Override
    public Collection<StoredDataMetadata> getHistoryFactoryList() {
        return storage.values().stream()/*.filter(factory -> !factory.metadata.id.equals(current))*/.map(item -> item.metadata).collect(Collectors.toList());
    }

    @Override
    public DataAndStoredMetadata<R> getCurrentFactory() {
        DataAndStoredMetadata<R> result = storage.get(current);
        return new DataAndStoredMetadata<>(result.root.internal().copy(),result.metadata);
    }

    @Override
    public void updateCurrentFactory(DataAndNewMetadata<R> update, String user, String comment) {
        update.root.internal().checkUsable();
        final StoredDataMetadata storedDataMetadata = new StoredDataMetadata();
        storedDataMetadata.creationTime=LocalDateTime.now();
        storedDataMetadata.id= UUID.randomUUID().toString();
        storedDataMetadata.user=user;
        storedDataMetadata.comment=comment;
        storedDataMetadata.baseVersionId=update.metadata.baseVersionId;
        storedDataMetadata.dataModelVersion=update.metadata.dataModelVersion;

        final DataAndStoredMetadata<R> updateData = new DataAndStoredMetadata<>(update.root, storedDataMetadata);
        storage.put(updateData.metadata.id, updateData);
        current=updateData.metadata.id;
    }

    @Override
    public DataAndNewMetadata<R> getPrepareNewFactory(){
        NewDataMetadata metadata = new NewDataMetadata();
        metadata.baseVersionId=current;
        return new DataAndNewMetadata<>(getCurrentFactory().root,metadata);
    }

    @Override
    public void loadInitialFactory() {
        current = UUID.randomUUID().toString();
        StoredDataMetadata metadata = new StoredDataMetadata();
        metadata.id=current;
        metadata.baseVersionId=current;
        metadata.user="System";
        storage.put(current,new DataAndStoredMetadata<>(initialFactory, metadata));
    }

    @Override
    public Collection<ScheduledDataMetadata> getFutureFactoryList() {
        return future.values().stream()/*.filter(factory -> !factory.metadata.id.equals(current))*/.map(item -> item.metadata).collect(Collectors.toList());
    }

    @Override
    public void deleteFutureFactory(String id) {
        future.remove(id);
    }

    public R getFutureFactory(String id) {
        DataAndScheduledMetadata<R> data = future.get(id);
        return data.root.internal().copyFromRoot();
    }

    @Override
    public void addFutureFactory(DataAndScheduledMetadata<R> futureUpdate, String user, String comment) {
        final ScheduledDataMetadata storedFactoryMetadata = new ScheduledDataMetadata();
        storedFactoryMetadata.creationTime=LocalDateTime.now();
        storedFactoryMetadata.id= UUID.randomUUID().toString();
        storedFactoryMetadata.user=user;
        storedFactoryMetadata.comment=comment;
        storedFactoryMetadata.baseVersionId=futureUpdate.metadata.baseVersionId;
        storedFactoryMetadata.dataModelVersion=futureUpdate.metadata.dataModelVersion;
        storedFactoryMetadata.scheduled = futureUpdate.metadata.scheduled;

        final DataAndScheduledMetadata<R> updateData = new DataAndScheduledMetadata<>(futureUpdate.root, storedFactoryMetadata);
        future.put(updateData.metadata.id, updateData);
    }

}
