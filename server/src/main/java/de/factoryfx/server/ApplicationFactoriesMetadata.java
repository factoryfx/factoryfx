package de.factoryfx.server;

import java.time.LocalDateTime;

import de.factoryfx.factory.FactoryBase;

public class ApplicationFactoriesMetadata<T extends FactoryBase<?,T>> {
    public LocalDateTime creationTime;
    public T root;

    /**the base version on the server*/
    public String baseVersionId;

}
