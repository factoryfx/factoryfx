package de.factoryfx.factory.attribute;

import com.fasterxml.jackson.annotation.JsonCreator;

public class IntegerAttribute extends ValueAttribute<Integer> {

    @JsonCreator
    public IntegerAttribute(Integer value) {
        set(value);
    }

    public IntegerAttribute() {

    }
}