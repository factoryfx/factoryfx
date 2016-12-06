package de.factoryfx.server;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.stream.Collectors;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.datastorage.FactoryAndStorageMetadata;
import de.factoryfx.factory.datastorage.FactoryStorage;
import de.factoryfx.factory.datastorage.StoredFactoryMetadata;


public class InMemoryFactoryStorage<L,V,T extends FactoryBase<L,V>> implements FactoryStorage<L,V,T> {
    private Map<String,FactoryAndStorageMetadata<T>> storage = new TreeMap<>();
    private String current;
    private T initialFactory;

    public InMemoryFactoryStorage(T initialFactory){
        this.initialFactory=initialFactory;
    }



    @Override
    public T getHistoryFactory(String id) {
        FactoryAndStorageMetadata<T> data = storage.get(id);
        return data.root.internal().copy().internal().prepareUsage();
    }

    @Override
    public Collection<StoredFactoryMetadata> getHistoryFactoryList() {
        return storage.values().stream()/*.filter(factory -> !factory.metadata.id.equals(current))*/.map(item -> item.metadata).collect(Collectors.toList());
    }

    @Override
    public FactoryAndStorageMetadata<T> getCurrentFactory() {
        FactoryAndStorageMetadata<T> result = storage.get(current);
        return new FactoryAndStorageMetadata<>(result.root.internal().prepareUsage(),result.metadata);
    }

    @Override
    public void updateCurrentFactory(FactoryAndStorageMetadata<T> update) {
        storage.put(update.metadata.id, update);
        current=update.metadata.id;
    }

    @Override
    public FactoryAndStorageMetadata<T> getPrepareNewFactory(){
        StoredFactoryMetadata metadata = new StoredFactoryMetadata();
        metadata.id=UUID.randomUUID().toString();
        metadata.baseVersionId=current;
        return new FactoryAndStorageMetadata<>(getCurrentFactory().root,metadata);
    }

    @Override
    public void loadInitialFactory() {
        current = UUID.randomUUID().toString();
        StoredFactoryMetadata metadata = new StoredFactoryMetadata();
        metadata.id=current;
        metadata.baseVersionId=current;
        metadata.user="System";
        storage.put(current,new FactoryAndStorageMetadata<>(initialFactory, metadata));
    }
}
