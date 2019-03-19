package io.github.factoryfx.data.attribute;

import io.github.factoryfx.data.Data;

public class DataReferenceListAttribute<T extends Data> extends ReferenceListAttribute<T,DataReferenceListAttribute<T>> {

    public DataReferenceListAttribute(Class<T> clazz) {
        super();
        setup(clazz);
    }

    /**
     * workaround for Data with generic parameter
     *
     * @param clazz content clazz
     * @param dummy must be null
     */
    public DataReferenceListAttribute(Class<T> clazz, Void dummy) {
        super();
        setup(clazz);
    }

}
