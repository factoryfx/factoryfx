package de.factoryfx.data.attribute.time;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonCreator;
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

}
