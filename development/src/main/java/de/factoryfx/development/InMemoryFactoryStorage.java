package de.factoryfx.development;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import de.factoryfx.datastorage.ApplicationFactoryMetadata;
import de.factoryfx.datastorage.FactoryStorage;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.LiveObject;


public class InMemoryFactoryStorage<T extends FactoryBase<? extends LiveObject, T>> implements FactoryStorage<T> {
    private Map<String,ApplicationFactoryMetadata<T>> storage = new TreeMap<>();
    private String current;
    private T initialFactory;

    public InMemoryFactoryStorage(T initialFactory){
        this.initialFactory=initialFactory;
    }


    @Override
    public ApplicationFactoryMetadata<T> getHistoryFactory(String id) {
        ApplicationFactoryMetadata<T> metadata = storage.get(id);
        ApplicationFactoryMetadata<T> result = new ApplicationFactoryMetadata<>(metadata.root.copy());
        result.baseVersionId=metadata.baseVersionId;
        return result;
    }

    @Override
    public Collection<ApplicationFactoryMetadata<T>> getHistoryFactoryList() {
        return storage.values();
    }

    @Override
    public ApplicationFactoryMetadata<T> getCurrentFactory() {
        ApplicationFactoryMetadata<T> factoriesMetadata = new ApplicationFactoryMetadata<>(storage.get(current).root.copy());
        factoriesMetadata.baseVersionId=current;
        return factoriesMetadata;
    }

    @Override
    public ApplicationFactoryMetadata<T> updateCurrentFactory(T factoryRoot) {
        String newId = UUID.randomUUID().toString();
        ApplicationFactoryMetadata<T> value = new ApplicationFactoryMetadata<>(factoryRoot.copy());
        storage.put(newId, value);
        current=newId;
        return value;
    }

    @Override
    public void loadInitialFactory() {
        current = UUID.randomUUID().toString();
        storage.put(current,new ApplicationFactoryMetadata<>(initialFactory));
    }
}
