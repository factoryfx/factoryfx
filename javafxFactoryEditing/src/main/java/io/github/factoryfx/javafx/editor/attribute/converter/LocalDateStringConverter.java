package io.github.factoryfx.javafx.editor.attribute.converter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.FormatStyle;
import java.util.Locale;

public class LocalDateStringConverter extends ParsingStringConverter<LocalDate> {

    @Override
    LocalDate fromNonEmptyString(String value) {
        DateTimeFormatter dtf = new DateTimeFormatterBuilder().appendLocalized(FormatStyle.SHORT, null).toFormatter(Locale.getDefault());
        try {
            return LocalDate.from(dtf.parse(value));
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString(LocalDate value) {
        // If the specified value is null, return a zero-length String
        if (value == null) {
            return "";
        }

        DateTimeFormatter dtf = new DateTimeFormatterBuilder().appendLocalized(FormatStyle.SHORT, null).toFormatter(Locale.getDefault());
        return dtf.format(value);
    }
}
