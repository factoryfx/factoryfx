package de.factoryfx.javafx.data.editor.attribute.converter;

import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.FormatStyle;
import java.util.Locale;

public class LocalDateStringConverter extends StringConverter<LocalDate> {

    /** {@inheritDoc} */
    @Override public LocalDate fromString(String value) {
        // If the specified value is null or zero-length, return null
        if (value == null) {
            return null;
        }

        value = value.trim();

        if (value.length() < 1) {
            return null;
        }

        DateTimeFormatter dtf = new DateTimeFormatterBuilder().appendLocalized(FormatStyle.SHORT, null).toFormatter(Locale.getDefault());
        try {
            return LocalDate.from(dtf.parse(value));
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /** {@inheritDoc} */
    @Override public String toString(LocalDate value) {
        // If the specified value is null, return a zero-length String
        if (value == null) {
            return "";
        }

        DateTimeFormatter dtf = new DateTimeFormatterBuilder().appendLocalized(FormatStyle.SHORT, null).toFormatter(Locale.getDefault());
        return dtf.format(value);
    }
}
