package de.factoryfx.data.attribute.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.ValueAttribute;

import java.time.LocalDate;

public class LocalDateAttribute extends ValueAttribute<LocalDate> {

    public LocalDateAttribute(AttributeMetadata attributeMetadata) {
        super(attributeMetadata,LocalDate.class);
    }

    @JsonCreator
    LocalDateAttribute(LocalDate initialValue) {
        super(null,LocalDate.class);
        set(initialValue);
    }

}
