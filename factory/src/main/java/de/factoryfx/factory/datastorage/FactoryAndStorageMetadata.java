package de.factoryfx.factory.datastorage;

import de.factoryfx.factory.FactoryBase;

public class FactoryAndStorageMetadata<T extends FactoryBase<?,?>> {
    public final T root;
    public final StoredFactoryMetadata metadata;

    public FactoryAndStorageMetadata(T root, StoredFactoryMetadata metadata) {
        this.root = root;
        this.metadata = metadata;
    }

    public FactoryAndStorageMetadata<T> copy(){
        return new FactoryAndStorageMetadata<>(root.<T>copy(),metadata);
    }
}
