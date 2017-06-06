package de.factoryfx.factory.atrribute;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.ReferenceAttribute;
import de.factoryfx.data.attribute.types.ObjectValueAttribute;
import de.factoryfx.factory.FactoryBase;

public class FactoryReferenceAttribute<L, T extends FactoryBase<? extends L,?>> extends ReferenceAttribute<T,FactoryReferenceAttribute<L,T>> {

    private Class<T> clazz;
    private AttributeMetadata attributeMetadata;

    @JsonCreator
    protected FactoryReferenceAttribute(T value) {
        super(value);
    }

    public FactoryReferenceAttribute(Class<T> clazz, AttributeMetadata attributeMetadata) {
        super(clazz, attributeMetadata);
        this.clazz=clazz;
        this.attributeMetadata=attributeMetadata;
    }

    public FactoryReferenceAttribute(AttributeMetadata attributeMetadata, Class clazz) {
        super(attributeMetadata, clazz);
    }

    public L instance(){
        if (get()==null){
            return null;
        }
        return get().internalFactory().instance();
    }

    @Override
    public Attribute<T> internal_copy() {
        return new FactoryReferenceAttribute<>(clazz,attributeMetadata);
    }
}
