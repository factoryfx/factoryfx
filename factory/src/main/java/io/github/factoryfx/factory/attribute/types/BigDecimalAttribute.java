package io.github.factoryfx.factory.attribute.types;

import java.math.BigDecimal;
import java.util.Objects;

import io.github.factoryfx.factory.attribute.ImmutableValueAttribute;

public class BigDecimalAttribute extends ImmutableValueAttribute<BigDecimal,BigDecimalAttribute> {

    private String decimalFormatPattern;

    public BigDecimalAttribute() {
        super();
    }

    public String internal_getDecimalFormatPattern() {
        return Objects.requireNonNullElse(decimalFormatPattern,"#,#");
    }

    /**
     * @see java.text.DecimalFormat
     * @param decimalFormatPattern pattern
     * @return self
     */
    public BigDecimalAttribute decimalFormatPattern(String decimalFormatPattern) {
        this.decimalFormatPattern=decimalFormatPattern;
        return this;
    }

}