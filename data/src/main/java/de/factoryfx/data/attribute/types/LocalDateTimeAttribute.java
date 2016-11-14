package de.factoryfx.data.attribute.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.ValueAttribute;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class LocalDateTimeAttribute extends ValueAttribute<LocalDateTime> {

    public LocalDateTimeAttribute(AttributeMetadata attributeMetadata) {
        super(attributeMetadata,LocalDateTime.class);
    }

    @JsonCreator
    LocalDateTimeAttribute(LocalDateTime initialValue) {
        super(null,LocalDateTime.class);
        set(initialValue);
    }

}
