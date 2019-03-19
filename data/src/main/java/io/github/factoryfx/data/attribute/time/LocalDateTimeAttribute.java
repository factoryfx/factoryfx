package io.github.factoryfx.data.attribute.time;

import java.time.LocalDateTime;

import io.github.factoryfx.data.attribute.ImmutableValueAttribute;

public class LocalDateTimeAttribute extends ImmutableValueAttribute<LocalDateTime,LocalDateTimeAttribute> {

    public LocalDateTimeAttribute() {
        super(LocalDateTime.class);
    }

}
