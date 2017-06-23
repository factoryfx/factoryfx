package de.factoryfx.data.attribute.types;

import java.net.URI;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.data.attribute.ImmutableValueAttribute;

public class URIAttribute extends ImmutableValueAttribute<URI,URIAttribute> {

    public URIAttribute() {
        super(URI.class);
    }

    @JsonCreator
    URIAttribute(URI initialValue) {
        super(URI.class);
        set(initialValue);
    }

}
