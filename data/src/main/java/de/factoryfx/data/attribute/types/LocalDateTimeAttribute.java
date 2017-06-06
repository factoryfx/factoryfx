package de.factoryfx.data.attribute.types;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.ImmutableValueAttribute;

public class LocalDateTimeAttribute extends ImmutableValueAttribute<LocalDateTime> {

    public LocalDateTimeAttribute(AttributeMetadata attributeMetadata) {
        super(attributeMetadata,LocalDateTime.class);
    }

    @JsonCreator
    LocalDateTimeAttribute(LocalDateTime initialValue) {
        super(null,LocalDateTime.class);
        set(initialValue);
    }

    @Override
    protected Attribute<LocalDateTime> createNewEmptyInstance() {
        return new LocalDateTimeAttribute(metadata);
    }
}
