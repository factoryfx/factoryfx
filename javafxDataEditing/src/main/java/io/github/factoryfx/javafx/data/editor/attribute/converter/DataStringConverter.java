package io.github.factoryfx.javafx.data.editor.attribute.converter;

import java.util.Optional;

import javafx.util.StringConverter;

import io.github.factoryfx.data.Data;

public class DataStringConverter<T extends Data> extends StringConverter<T> {
    @Override
    public String toString(Data object) {
        return Optional.ofNullable(object).map(d->d.internal().getDisplayText()).orElse("<EMPTY>");
    }

    @Override
    public T fromString(String string) {
        throw new UnsupportedOperationException();
    }
}
