package de.factoryfx.data.attribute.types;

import java.net.URI;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.data.attribute.ValueListAttribute;

public class URIListAttribute extends ValueListAttribute<URI,URIListAttribute> {
    @JsonCreator
    public URIListAttribute() {
        super(URI.class);
    }

}
