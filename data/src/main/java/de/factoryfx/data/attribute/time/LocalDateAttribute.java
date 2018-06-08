package de.factoryfx.data.attribute.time;

import de.factoryfx.data.attribute.ImmutableValueAttribute;

import java.time.LocalDate;

public class LocalDateAttribute extends ImmutableValueAttribute<LocalDate,LocalDateAttribute> {

    public LocalDateAttribute() {
        super(LocalDate.class);
    }

}
