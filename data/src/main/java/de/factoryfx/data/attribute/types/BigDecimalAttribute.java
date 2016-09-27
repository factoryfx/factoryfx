package de.factoryfx.data.attribute.types;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.ValueAttribute;

public class BigDecimalAttribute extends ValueAttribute<BigDecimal> {

    @JsonCreator
    BigDecimalAttribute(BigDecimal value) {
        super(null,BigDecimal.class);
        set(value);
    }

    public BigDecimalAttribute(AttributeMetadata attributeMetadata) {
        super(attributeMetadata,BigDecimal.class);
    }
}