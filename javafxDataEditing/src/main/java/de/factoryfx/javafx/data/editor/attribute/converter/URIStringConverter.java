package de.factoryfx.javafx.data.editor.attribute.converter;

import java.net.URI;
import java.net.URISyntaxException;

public class URIStringConverter extends ParsingStringConverter<URI> {

    @Override
    URI fromNonEmptyString(String value) {
        try {
            return new URI(value);
        } catch (URISyntaxException e) {
            return null;
        }
    }

    @Override
    public String toString(URI value) {
        if (value == null) {
            return "";
        }
        return value.toString();
    }
}
