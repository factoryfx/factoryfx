package de.factoryfx.data.attribute.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.data.attribute.AttributeJsonWrapper;
import de.factoryfx.data.attribute.AttributeTypeInfo;
import de.factoryfx.data.attribute.ImmutableValueAttribute;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EnumAttribute<T extends Enum<T>> extends ImmutableValueAttribute<T,EnumAttribute<T>> {

    @JsonCreator
    EnumAttribute(T value) {
        super(null);
        set(value);
    }
    private Class<T> clazz;

    public EnumAttribute(Class<T> clazz) {
        super(clazz);
        this.clazz=clazz;
    }

    protected EnumAttribute() {
        super(null);
    }

    @Override
    public AttributeTypeInfo internal_getAttributeType() {

        return new AttributeTypeInfo(clazz,null,null,AttributeTypeInfo.AttributeTypeCategory.VALUE);
    }

    @Override
    public EnumAttribute<T> internal_copy() {
        final EnumAttribute<T> result = new EnumAttribute<>(clazz);
        result.set(get());
        return result;
    }

    @Override
    protected EnumAttribute<T> createNewEmptyInstance() {
        return new EnumAttribute<>(clazz);
    }

    public Class<T> internal_getEnumClass() {
        return clazz;
    }


    public List<Enum> internal_possibleEnumValues() {
        return new ArrayList<>(Arrays.asList(clazz.getEnumConstants()));
    }

    @Override
    public void internal_writeToJsonWrapper(AttributeJsonWrapper attributeJsonWrapper) {
        super.internal_writeToJsonWrapper(attributeJsonWrapper);
        attributeJsonWrapper.enumClazz = clazz;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void internal_readFromJsonWrapper(AttributeJsonWrapper attributeJsonWrapper) {
        super.internal_readFromJsonWrapper(attributeJsonWrapper);
        clazz= (Class<T>) attributeJsonWrapper.enumClazz;
    }
}
