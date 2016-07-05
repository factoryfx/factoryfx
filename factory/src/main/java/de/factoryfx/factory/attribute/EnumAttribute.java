package de.factoryfx.factory.attribute;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;

public class EnumAttribute<T extends Enum<T>> extends ValueAttribute<T,EnumAttribute<T>> {

    @JsonCreator
    EnumAttribute(T value) {
        super(null);
        set(value);
    }
    private Class<T> clazz;

    public EnumAttribute(Class<T> clazz, AttributeMetadata attributeMetadata) {
        super(attributeMetadata);
        this.clazz=clazz;
    }

    public List<Enum<T>> getEnumValues(){
        return Arrays.asList(clazz.getEnumConstants());
    }
}
