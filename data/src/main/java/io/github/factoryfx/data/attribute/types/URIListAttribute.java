package io.github.factoryfx.data.attribute.types;

import java.net.URI;
import java.net.URISyntaxException;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.factoryfx.data.attribute.ValueListAttribute;

public class URIListAttribute extends ValueListAttribute<URI,URIListAttribute> {

    public URIListAttribute() {
        super(URI.class);
    }

    /** workaround for checked exception
     * @param uri uri as string
     * */
    @JsonIgnore
    public void addUnchecked(String uri){
        try {
            add(new URI(uri));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
