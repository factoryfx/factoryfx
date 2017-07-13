package de.factoryfx.factory.datastorage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.factoryfx.factory.FactoryBase;

public class FactoryAndNewMetadata<T extends FactoryBase<?,?>> {
    public final T root;
    public final NewFactoryMetadata metadata;

    @JsonCreator
    public FactoryAndNewMetadata(@JsonProperty("root") T root, @JsonProperty("metadata")NewFactoryMetadata metadata) {
        this.root = root;
        this.metadata = metadata;
    }

}
