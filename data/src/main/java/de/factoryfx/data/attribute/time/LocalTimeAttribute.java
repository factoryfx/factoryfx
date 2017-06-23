package de.factoryfx.data.attribute.time;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.data.attribute.ImmutableValueAttribute;

import java.time.Duration;
import java.time.LocalTime;

public class LocalTimeAttribute extends ImmutableValueAttribute<LocalTime,LocalTimeAttribute> {

    public LocalTimeAttribute() {
        super(LocalTime.class);
    }

    @JsonCreator
    LocalTimeAttribute(LocalTime initialValue) {
        super(LocalTime.class);
        set(initialValue);
    }

    @Override
    protected LocalTimeAttribute createNewEmptyInstance() {
        return new LocalTimeAttribute();
    }
}
