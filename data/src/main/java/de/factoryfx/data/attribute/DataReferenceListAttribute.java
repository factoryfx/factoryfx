package de.factoryfx.data.attribute;

import de.factoryfx.data.Data;

public class DataReferenceListAttribute<T extends Data> extends ReferenceListAttribute<T,DataReferenceListAttribute<T>> {

    public DataReferenceListAttribute() {
        super();
    }

    public DataReferenceListAttribute(Class<T> clazz) {
        super();
        setup(clazz);
    }

    @Override
    public DataReferenceListAttribute<T> setup(Class<T> clazz) {
        return super.setup(clazz);
    }

    @Override
    public DataReferenceListAttribute<T> setupUnsafe(Class clazz) {
        return super.setupUnsafe(clazz);
    }
}
