package de.factoryfx.data.attribute.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.ImmutableValueAttribute;

import java.time.LocalDate;

public class LocalDateAttribute extends ImmutableValueAttribute<LocalDate> {

    public LocalDateAttribute(AttributeMetadata attributeMetadata) {
        super(attributeMetadata,LocalDate.class);
    }

    @JsonCreator
    LocalDateAttribute(LocalDate initialValue) {
        super(null,LocalDate.class);
        set(initialValue);
    }

    @Override
    protected Attribute<LocalDate> createNewEmptyInstance() {
        return new LocalDateAttribute(metadata);
    }
}
