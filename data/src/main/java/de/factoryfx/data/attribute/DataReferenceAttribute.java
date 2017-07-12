package de.factoryfx.data.attribute;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.data.Data;

public class DataReferenceAttribute<T extends Data> extends ReferenceAttribute<T,DataReferenceAttribute<T>> {


    @JsonCreator
    protected DataReferenceAttribute(T value) {
        super(value);
    }

    public DataReferenceAttribute(Class<T> clazz) {
        super();
        setup(clazz);
    }

    public DataReferenceAttribute() {
        super();
    }

    @Override
    public DataReferenceAttribute<T> setup(Class<T> clazz){
        return super.setup(clazz);
    }

    @Override
    public DataReferenceAttribute<T> setupUnsafe(Class clazz) {
        return super.setupUnsafe(clazz);
    }
}
