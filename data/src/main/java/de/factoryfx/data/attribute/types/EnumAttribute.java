package de.factoryfx.data.attribute.types;

import com.fasterxml.jackson.annotation.*;
import de.factoryfx.data.attribute.AttributeTypeInfo;
import de.factoryfx.data.attribute.ImmutableValueAttribute;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class EnumAttribute<T extends Enum<T>> extends ImmutableValueAttribute<EnumAttribute.EnumWrapper<T>,EnumAttribute<T>> {

    private T anEnum;

    @JsonCreator
    EnumAttribute(EnumWrapper<T> value) {
        super(null);
        set(value);
    }
    private Class<T> clazz;

    @SuppressWarnings("unchecked")
    public EnumAttribute(Class<T> clazz) {
        super((Class<EnumWrapper<T>>) EnumWrapper.class.asSubclass(EnumWrapper.class));//workaround for java generic bug
        this.clazz=clazz;
    }

    @Override
    public AttributeTypeInfo internal_getAttributeType() {

        return new AttributeTypeInfo(clazz,null,null,AttributeTypeInfo.AttributeTypeCategory.VALUE);
    }

    public List<Enum> internal_possibleEnumValues() {
        return new ArrayList<>(Arrays.asList(clazz.getEnumConstants()));
    }

    public T getEnum() {
        return get().enumField;
    }

    @SuppressWarnings("unchecked")
    EnumAttribute<T> defaultEnum(T anEnum){
        set(new EnumWrapper<T>(anEnum, (Class<T>) anEnum.getClass()));
        return this;
    }


    //Workaround for bug https://github.com/FasterXML/jackson-databind/issues/937
    //@JsonValue doesn't work with JsonTypeInfo
    public static class EnumWrapper<T extends Enum<T>>{
        @JsonProperty
        private final T enumField;
        @JsonProperty
        private final Class<T> enumClass;


        public EnumWrapper(T enumField, Class<T> enumClass) {
            this.enumField = enumField;
            this.enumClass=enumClass;
        }

        @JsonCreator
        protected EnumWrapper(@JsonProperty("enumField")String enumField, @JsonProperty("enumClass")Class enumClass) {
            this.enumClass= (Class<T>) enumClass;
            this.enumField = Arrays.stream(this.enumClass.getEnumConstants()).filter(t -> t.name().equals(enumField)).findAny().orElseGet(null);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            EnumWrapper<?> that = (EnumWrapper<?>) o;

            if (enumField != null ? !enumField.equals(that.enumField) : that.enumField != null) return false;
            return enumClass != null ? enumClass.equals(that.enumClass) : that.enumClass == null;
        }

        @Override
        public int hashCode() {
            int result = enumField != null ? enumField.hashCode() : 0;
            result = 31 * result + (enumClass != null ? enumClass.hashCode() : 0);
            return result;
        }
    }



}
