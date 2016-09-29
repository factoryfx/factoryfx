package de.factoryfx.data.attribute.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.AttributeTypeInfo;
import de.factoryfx.data.attribute.ValueAttribute;

public class EnumAttribute<T extends Enum<T>> extends ValueAttribute<T> {

    @JsonCreator
    EnumAttribute(T value) {
        super(null,null);
        set(value);
    }
    private Class<T> clazz;

    public EnumAttribute(Class<T> clazz, AttributeMetadata attributeMetadata) {
        super(attributeMetadata,clazz);
        this.clazz=clazz;
    }

    @Override
    public AttributeTypeInfo getAttributeType() {

        return new AttributeTypeInfo(clazz,null,null,AttributeTypeInfo.AttributeTypeCategory.VALUE);
    }
}