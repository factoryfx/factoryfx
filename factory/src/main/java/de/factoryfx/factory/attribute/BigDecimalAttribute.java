package de.factoryfx.factory.attribute;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonCreator;

public class BigDecimalAttribute extends ValueAttribute<BigDecimal,BigDecimalAttribute> {

    @JsonCreator
    BigDecimalAttribute(BigDecimal value) {
        super(null,BigDecimal.class);
        set(value);
    }

    public BigDecimalAttribute(AttributeMetadata attributeMetadata) {
        super(attributeMetadata,BigDecimal.class);
    }
}