package de.factoryfx.factory.datastorage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.factoryfx.factory.FactoryBase;

public class FactoryAndScheduledMetadata<T extends FactoryBase<?,?>> {
    public final T root;
    public final ScheduledFactoryMetadata metadata;

    @JsonCreator
    public FactoryAndScheduledMetadata(@JsonProperty("root") T root, @JsonProperty("metadata")ScheduledFactoryMetadata metadata) {
        this.root = root;
        this.metadata = metadata;
    }
}
