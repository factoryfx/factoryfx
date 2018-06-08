package de.factoryfx.data.attribute.time;

import de.factoryfx.data.attribute.ImmutableValueAttribute;

import java.time.LocalTime;

public class LocalTimeAttribute extends ImmutableValueAttribute<LocalTime,LocalTimeAttribute> {

    public LocalTimeAttribute() {
        super(LocalTime.class);
    }

}
