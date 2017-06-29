package de.factoryfx.javafx.editor.attribute.converter;

import java.time.temporal.ChronoUnit;

import javafx.util.StringConverter;

/**
 * Created by mhavlik on 29.06.17.
 */
public class ChronoUnitStringConverter extends StringConverter<ChronoUnit> {
    @Override
    public String toString(ChronoUnit object) {
        return object == null ? null : object.name();
    }

    @Override
    public ChronoUnit fromString(String string) {
        return string == null ? null : ChronoUnit.valueOf(string);
    }
}
