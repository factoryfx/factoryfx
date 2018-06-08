package de.factoryfx.data.attribute.time;

import java.time.LocalDateTime;

import de.factoryfx.data.attribute.ImmutableValueAttribute;

public class LocalDateTimeAttribute extends ImmutableValueAttribute<LocalDateTime,LocalDateTimeAttribute> {

    public LocalDateTimeAttribute() {
        super(LocalDateTime.class);
    }

}
