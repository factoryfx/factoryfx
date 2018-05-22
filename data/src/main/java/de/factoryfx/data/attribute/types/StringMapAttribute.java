package de.factoryfx.data.attribute.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.data.attribute.ValueMapAttribute;

public class StringMapAttribute extends ValueMapAttribute<String,String,StringMapAttribute> {
    @JsonCreator
    public StringMapAttribute() {
        super(String.class, String.class);
    }

}
