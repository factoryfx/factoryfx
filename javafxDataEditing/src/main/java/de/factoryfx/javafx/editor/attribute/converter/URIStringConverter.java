package de.factoryfx.javafx.editor.attribute.converter;

import javafx.util.StringConverter;

import java.net.URI;
import java.net.URISyntaxException;

public class URIStringConverter extends StringConverter<URI> {

    /** {@inheritDoc} */
    @Override public URI fromString(String value) {
        // If the specified value is null or zero-length, return null
        if (value == null) {
            return null;
        }

        value = value.trim();

        if (value.length() < 1) {
            return null;
        }

        try {
            return new URI(value);
        } catch (URISyntaxException e) {
            return null;
        }
    }

    /** {@inheritDoc} */
    @Override public String toString(URI value) {
        // If the specified value is null, return a zero-length String
        if (value == null) {
            return "";
        }

        return value.toString();
    }
}
