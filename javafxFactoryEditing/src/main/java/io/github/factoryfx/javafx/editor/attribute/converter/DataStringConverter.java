package io.github.factoryfx.javafx.editor.attribute.converter;

import java.util.Optional;

import io.github.factoryfx.factory.FactoryBase;
import javafx.util.StringConverter;


public class DataStringConverter<T extends FactoryBase<?,?>> extends StringConverter<T> {
    @Override
    public String toString(T object) {
        return Optional.ofNullable(object).map(d->d.internal().getDisplayText()).orElse("<EMPTY>");
    }

    @Override
    public T fromString(String string) {
        throw new UnsupportedOperationException();
    }
}
