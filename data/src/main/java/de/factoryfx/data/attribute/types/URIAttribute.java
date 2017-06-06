package de.factoryfx.data.attribute.types;

import java.net.URI;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.ImmutableValueAttribute;

public class URIAttribute extends ImmutableValueAttribute<URI> {

    public URIAttribute(AttributeMetadata attributeMetadata) {
        super(attributeMetadata,URI.class);
    }

    @JsonCreator
    URIAttribute(URI initialValue) {
        super(null,URI.class);
        set(initialValue);
    }

    @Override
    protected Attribute<URI> createNewEmptyInstance() {
        return new URIAttribute(metadata);
    }
}
