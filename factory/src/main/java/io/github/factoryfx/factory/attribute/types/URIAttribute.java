package io.github.factoryfx.factory.attribute.types;

import java.net.URI;
import java.net.URISyntaxException;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.factoryfx.factory.attribute.ImmutableValueAttribute;

public class URIAttribute extends ImmutableValueAttribute<URI,URIAttribute> {

    public URIAttribute() {
        super(URI.class);
    }

    /** workaround for checked exception
     * @param uri uri as string
     * */
    @JsonIgnore
    public void setUnchecked(String uri){
        try {
            set(new URI(uri));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

}
