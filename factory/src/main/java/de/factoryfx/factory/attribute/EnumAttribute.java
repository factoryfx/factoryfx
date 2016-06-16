package de.factoryfx.factory.attribute;

import com.fasterxml.jackson.annotation.JsonCreator;

public class EnumAttribute<T extends Enum<T>> extends ValueAttribute<T> {

    @JsonCreator
    public EnumAttribute(T value) {
        set(value);
    }
}
