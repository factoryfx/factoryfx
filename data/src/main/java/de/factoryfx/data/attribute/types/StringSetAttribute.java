package de.factoryfx.data.attribute.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.data.attribute.ValueSetAttribute;

import java.util.Set;

public class StringSetAttribute extends ValueSetAttribute<String,StringSetAttribute> {
    @JsonCreator
    public StringSetAttribute() {
        super(String.class);
    }


    @Override
    protected StringSetAttribute createNewEmptyInstance() {
        return new StringSetAttribute();
    }
}
