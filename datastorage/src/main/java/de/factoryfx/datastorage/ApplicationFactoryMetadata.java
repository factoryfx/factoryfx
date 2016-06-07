package de.factoryfx.datastorage;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.factoryfx.factory.FactoryBase;

public class ApplicationFactoryMetadata<T extends FactoryBase<?,T>> {
    public LocalDateTime creationTime;
    public T root;

    /**the base version on the server*/
    public String baseVersionId;


    @JsonCreator
    public ApplicationFactoryMetadata(@JsonProperty("root")T root) {
        this.root = root;
    }
}
