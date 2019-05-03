package io.github.factoryfx.javafx.editor.attribute.converter;

import javafx.util.StringConverter;

public abstract class ParsingStringConverter<T> extends StringConverter<T> {

    @Override
    public T fromString(String value) {
        if (value == null) {
            return null;
        }

        value = value.trim();

        if (value.length() < 1) {
            return null;
        }
        return fromNonEmptyString(value);
    }

    abstract T fromNonEmptyString(String value);
}
