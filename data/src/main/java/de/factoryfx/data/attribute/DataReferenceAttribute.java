package de.factoryfx.data.attribute;

import de.factoryfx.data.Data;

public class DataReferenceAttribute<T extends Data> extends ReferenceAttribute<T,DataReferenceAttribute<T>> {

    /**
     *
     * @param clazz content clazz
     */
    public DataReferenceAttribute(Class<T> clazz) {
        super();
        setup(clazz);
    }

    /**
     * workaround for Data with generic parameter
     *
     * @param clazz content clazz
     * @param dummy must be null
     */
    public DataReferenceAttribute(Class clazz,Void dummy) {
        super();
        setupUnsafe(clazz);
    }

}
