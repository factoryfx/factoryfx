package de.factoryfx.factory.attribute.util;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.factory.attribute.AttributeMetadata;
import de.factoryfx.factory.attribute.ValueAttribute;

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