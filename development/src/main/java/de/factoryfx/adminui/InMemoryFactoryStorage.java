package de.factoryfx.adminui;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.stream.Collectors;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.LiveObject;
import de.factoryfx.factory.datastorage.FactoryAndStorageMetadata;
import de.factoryfx.factory.datastorage.FactoryStorage;
import de.factoryfx.factory.datastorage.StoredFactoryMetadata;


public class InMemoryFactoryStorage<T extends FactoryBase<? extends LiveObject<?>, T>> implements FactoryStorage<T> {
    private Map<String,FactoryAndStorageMetadata<T>> storage = new TreeMap<>();
    private String current;
    private T initialFactory;

    public InMemoryFactoryStorage(T initialFactory){
        this.initialFactory=initialFactory;
    }


    @Override
    public T getHistoryFactory(String id) {
        FactoryAndStorageMetadata<T> data = storage.get(id);
        return data.root.copy();
    }

    @Override
    public Collection<StoredFactoryMetadata> getHistoryFactoryList() {
        return storage.values().stream().filter(factory -> !factory.metadata.id.equals(current)).map(item -> item.metadata).collect(Collectors.toList());
    }

    @Override
    public FactoryAndStorageMetadata<T> getCurrentFactory() {
        FactoryAndStorageMetadata<T> result = storage.get(current);
        return result.copy();
    }

    @Override
    public void updateCurrentFactory(T factoryRoot, String user) {
        String newId = UUID.randomUUID().toString();
        StoredFactoryMetadata metadata = new StoredFactoryMetadata();
        metadata.id=newId;
        metadata.baseVersionId=current;
        metadata.user=user;
        FactoryAndStorageMetadata<T> value = new FactoryAndStorageMetadata<>(factoryRoot.copy(), metadata);
        storage.put(newId, value);
        current=newId;
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
