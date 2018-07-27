package de.factoryfx.microservice.common;

import de.factoryfx.data.Data;
import de.factoryfx.data.storage.DataAndNewMetadata;

/**
 * @param <R> root data type
 */
public class UpdateCurrentFactoryRequest<R extends Data> {
    public DataAndNewMetadata<R> factoryUpdate;
    public String comment;
}
