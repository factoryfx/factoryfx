package de.factoryfx.data.attribute.types;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.data.attribute.ImmutableValueAttribute;

public class BigDecimalAttribute extends ImmutableValueAttribute<BigDecimal,BigDecimalAttribute> {

    private String decimalFormatPattern="#,#";

    @JsonCreator
    BigDecimalAttribute(BigDecimal value) {
        super(BigDecimal.class);
        set(value);
    }

    public BigDecimalAttribute() {
        super(BigDecimal.class);
    }

    public String internal_getDecimalFormatPattern() {
        return decimalFormatPattern;
    }

    public void decimalFormatPatter(String decimalFormatPattern) {
        this.decimalFormatPattern=decimalFormatPattern;
    }

}