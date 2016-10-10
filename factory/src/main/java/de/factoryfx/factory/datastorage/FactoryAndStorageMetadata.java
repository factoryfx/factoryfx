package de.factoryfx.factory.datastorage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.factoryfx.factory.FactoryBase;

public class FactoryAndStorageMetadata<T extends FactoryBase<?,?>> {
    public final T root;
    public final StoredFactoryMetadata metadata;

    @JsonCreator
    public FactoryAndStorageMetadata(@JsonProperty("root") T root, @JsonProperty("metadata")StoredFactoryMetadata metadata) {
        this.root = root;
        this.metadata = metadata;
    }

    public FactoryAndStorageMetadata<T> copy(){
        return new FactoryAndStorageMetadata<>(root.<T>copy(),metadata);
    }
}
