package io.github.factoryfx.javafx.editor.attribute.converter;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;

public class BigDecimalStringConverter extends ParsingStringConverter<BigDecimal> {
    private final DecimalFormat decimalFormat;

    public BigDecimalStringConverter(DecimalFormat decimalFormat) {
        this.decimalFormat = decimalFormat;
    }


    @Override
    BigDecimal fromNonEmptyString(String value) {
        try {
            return (BigDecimal) decimalFormat.parse(value);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString(BigDecimal value) {
        if (value == null) {
            return "";
        }
        return decimalFormat.format(value);
    }
}
