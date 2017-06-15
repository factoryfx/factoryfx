package de.factoryfx.data.attribute.types;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.data.attribute.ImmutableValueAttribute;

public class LocalDateTimeAttribute extends ImmutableValueAttribute<LocalDateTime,LocalDateTimeAttribute> {

    public LocalDateTimeAttribute() {
        super(LocalDateTime.class);
    }

    @JsonCreator
    LocalDateTimeAttribute(LocalDateTime initialValue) {
        super(LocalDateTime.class);
        set(initialValue);
    }

    @Override
    protected LocalDateTimeAttribute createNewEmptyInstance() {
        return new LocalDateTimeAttribute();
    }
}
