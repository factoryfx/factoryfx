package de.factoryfx.data.attribute.types;

import java.net.URI;
import java.net.URISyntaxException;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
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

    /** workaround for checked exception */
    @JsonIgnore
    public void setUnchecked(String uri){
        try {
            set(new URI(uri));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

}
