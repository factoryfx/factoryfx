package de.factoryfx.data.attribute.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.data.attribute.ValueSetAttribute;

public class StringSetAttribute extends ValueSetAttribute<String,StringSetAttribute> {
    @JsonCreator
    public StringSetAttribute() {
        super(String.class);
    }

}
