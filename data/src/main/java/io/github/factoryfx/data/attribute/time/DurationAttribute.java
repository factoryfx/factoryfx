package io.github.factoryfx.data.attribute.time;

import java.time.Duration;

import io.github.factoryfx.data.attribute.ImmutableValueAttribute;


public class DurationAttribute extends ImmutableValueAttribute<Duration,DurationAttribute> {

    public DurationAttribute() {
        super(Duration.class);
    }


}
