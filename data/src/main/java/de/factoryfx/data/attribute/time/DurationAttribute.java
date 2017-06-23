package de.factoryfx.data.attribute.time;

import java.time.Duration;

import com.fasterxml.jackson.annotation.JsonCreator;

import de.factoryfx.data.attribute.ImmutableValueAttribute;


public class DurationAttribute extends ImmutableValueAttribute<Duration,DurationAttribute> {

    public DurationAttribute() {
        super(Duration.class);
    }

    @JsonCreator
    DurationAttribute(Duration initialValue) {
        super(Duration.class);
        set(initialValue);
    }

}
