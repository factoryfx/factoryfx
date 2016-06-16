package de.factoryfx.factory.attribute;

import com.fasterxml.jackson.annotation.JsonCreator;

public class StringAttribute extends ValueAttribute<String> {

    public StringAttribute() {

    }

    @JsonCreator
    public StringAttribute(String defaultValue) {
        set(defaultValue);
    }



}
