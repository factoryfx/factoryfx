package de.factoryfx.javafx.data.editor.attribute.converter;

import java.util.Optional;

import javafx.util.StringConverter;

import de.factoryfx.data.Data;

public class DataStringConverter extends StringConverter<Data> {
    @Override
    public String toString(Data object) {
        return Optional.ofNullable(object).map(d->d.internal().getDisplayText()).orElse("<EMPTY>");
    }

    @Override
    public Data fromString(String string) {
        throw new UnsupportedOperationException();
    }
}
