package de.factoryfx.factory.attribute;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;

public class EnumAttribute<T extends Enum<T>> extends ValueAttribute<T,EnumAttribute<T>> {

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

    private List<Enum<T>> getEnumValues(){
        return Arrays.asList(clazz.getEnumConstants());
    }

    @Override
    public AttributeTypeInfo getAttributeType() {

        return new AttributeTypeInfo(clazz,null,null,AttributeTypeInfo.AttributeTypeCategory.VALUE);
    }
}
