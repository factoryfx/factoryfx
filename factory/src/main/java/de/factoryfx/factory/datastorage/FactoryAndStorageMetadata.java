package de.factoryfx.factory.datastorage;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.LiveObject;

public class FactoryAndStorageMetadata<T extends FactoryBase<? extends LiveObject<?>, T>> {
    public final T root;
    public final StoredFactoryMetadata metadata;

    public FactoryAndStorageMetadata(T root, StoredFactoryMetadata metadata) {
        this.root = root;
        this.metadata = metadata;
    }

    public FactoryAndStorageMetadata<T> copy(){
        return new FactoryAndStorageMetadata<>(root.copy(),metadata);
    }
}
