package de.factoryfx.data.attribute.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.ValueMapAttribute;

public class StringMapAttribute extends ValueMapAttribute<String,String> {

    public StringMapAttribute(AttributeMetadata attributeMetadata) {
        super(attributeMetadata, String.class, String.class);
    }

    @JsonCreator
    StringMapAttribute() {
        super(null,null,null);
    }
}
