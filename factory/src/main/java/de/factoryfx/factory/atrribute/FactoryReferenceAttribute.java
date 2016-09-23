package de.factoryfx.factory.atrribute;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.ReferenceAttribute;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.LiveObject;

public class FactoryReferenceAttribute<L extends LiveObject, T extends FactoryBase<L>> extends ReferenceAttribute<T> {

    @JsonCreator
    protected FactoryReferenceAttribute(T value) {
        super(value);
    }

    public FactoryReferenceAttribute(Class<T> clazz, AttributeMetadata attributeMetadata) {
        super(clazz, attributeMetadata);
    }

    public FactoryReferenceAttribute(AttributeMetadata attributeMetadata, Class clazz) {
        super(attributeMetadata, clazz);
    }

    public L instance(){
        if (get()==null){
            return null;
        }
        return get().instance();
    }
}
