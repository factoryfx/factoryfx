package de.factoryfx.data.attribute.types;

import java.net.URI;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.ValueListAttribute;

public class URIListAttribute extends ValueListAttribute<URI> {

    public URIListAttribute(AttributeMetadata attributeMetadata) {
        super(URI.class,attributeMetadata);
    }

    @JsonCreator
    URIListAttribute() {
        super(null,(AttributeMetadata)null);
    }
}
