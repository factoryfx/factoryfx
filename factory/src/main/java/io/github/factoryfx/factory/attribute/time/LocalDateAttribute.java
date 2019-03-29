package io.github.factoryfx.factory.attribute.time;

import io.github.factoryfx.factory.attribute.ImmutableValueAttribute;

import java.time.LocalDate;

public class LocalDateAttribute extends ImmutableValueAttribute<LocalDate,LocalDateAttribute> {

    public LocalDateAttribute() {
        super(LocalDate.class);
    }

}
