package de.factoryfx.data.attribute.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.ValueSetAttribute;

public class StringSetAttribute extends ValueSetAttribute<String> {

    public StringSetAttribute(AttributeMetadata attributeMetadata) {
        super(String.class,attributeMetadata);
    }

    @JsonCreator
    StringSetAttribute() {
        super(null,null);
    }

}
