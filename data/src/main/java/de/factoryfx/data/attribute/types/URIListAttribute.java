package de.factoryfx.data.attribute.types;

import java.net.URI;
import java.net.URISyntaxException;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.ValueListAttribute;

public class URIListAttribute extends ValueListAttribute<URI> {

    static final URI DEFAULT_URI;
    static {
        try {
            DEFAULT_URI = new URI("http://www.google.com");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public URIListAttribute(AttributeMetadata attributeMetadata) {
        super(URI.class,attributeMetadata,DEFAULT_URI);
    }

    @JsonCreator
    URIListAttribute() {
        super(null,null,null);
    }
}
