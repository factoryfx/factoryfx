package de.factoryfx.data.attribute.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.ValueListAttribute;
import de.factoryfx.data.jackson.ObservableListJacksonAbleWrapper;

import java.net.URI;
import java.net.URISyntaxException;

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
    URIListAttribute(ObservableListJacksonAbleWrapper<URI> list) {
        super(null,null,null);
        set(list.unwrap());
    }
}
