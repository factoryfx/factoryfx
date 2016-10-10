package de.factoryfx.data.attribute.types;

import java.net.URI;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.ValueAttribute;

public class URIAttribute extends ValueAttribute<URI> {

    public URIAttribute(AttributeMetadata attributeMetadata) {
        super(attributeMetadata,URI.class);
    }

    @JsonCreator
    URIAttribute(URI initialValue) {
        super(null,URI.class);
        set(initialValue);
    }

}
