package io.github.factoryfx.data.attribute.time;

import io.github.factoryfx.data.attribute.ImmutableValueAttribute;

import java.time.LocalDate;

public class LocalDateAttribute extends ImmutableValueAttribute<LocalDate,LocalDateAttribute> {

    public LocalDateAttribute() {
        super(LocalDate.class);
    }

}
