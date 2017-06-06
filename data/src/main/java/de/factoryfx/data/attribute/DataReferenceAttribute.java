package de.factoryfx.data.attribute;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import de.factoryfx.data.Data;

import java.util.*;
import java.util.function.Function;

public class DataReferenceAttribute<T extends Data> extends ReferenceAttribute<T,DataReferenceAttribute<T>> {


    @JsonCreator
    protected DataReferenceAttribute(T value) {
        super(null,(AttributeMetadata)null);
        set(value);
    }

    /**
     * @see ReferenceBaseAttribute#ReferenceBaseAttribute(Class ,AttributeMetadata)
     * */
    public DataReferenceAttribute(Class<T> clazz, AttributeMetadata attributeMetadata) {
        super(clazz,attributeMetadata);
    }

    /**
     * @see ReferenceBaseAttribute#ReferenceBaseAttribute(AttributeMetadata,Class)
     * */
    @SuppressWarnings("unchecked")
    public DataReferenceAttribute(AttributeMetadata attributeMetadata, Class clazz) {
        super(attributeMetadata,clazz);
    }

    @Override
    public Attribute<T> internal_copy() {
        final DataReferenceAttribute<T> result = new DataReferenceAttribute<>(containingFactoryClass, metadata);
        result.set(get());
        return result;
    }

}
