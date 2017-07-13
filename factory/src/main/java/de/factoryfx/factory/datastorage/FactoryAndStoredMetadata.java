package de.factoryfx.factory.datastorage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.factoryfx.factory.FactoryBase;

public class FactoryAndStoredMetadata<T extends FactoryBase<?,?>> {
    public final T root;
    public final StoredFactoryMetadata metadata;

    @JsonCreator
    public FactoryAndStoredMetadata(@JsonProperty("root") T root, @JsonProperty("metadata")StoredFactoryMetadata metadata) {
        this.root = root;
        this.metadata = metadata;
    }
}
