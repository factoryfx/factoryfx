package io.github.factoryfx.factory.attribute.time;

import java.time.LocalDateTime;

import io.github.factoryfx.factory.attribute.ImmutableValueAttribute;

public class LocalDateTimeAttribute extends ImmutableValueAttribute<LocalDateTime,LocalDateTimeAttribute> {

    public LocalDateTimeAttribute() {
        super(LocalDateTime.class);
    }

}
