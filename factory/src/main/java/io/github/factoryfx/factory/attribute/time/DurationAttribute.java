package io.github.factoryfx.factory.attribute.time;

import java.time.Duration;

import io.github.factoryfx.factory.attribute.ImmutableValueAttribute;


public class DurationAttribute extends ImmutableValueAttribute<Duration,DurationAttribute> {

    public DurationAttribute() {
        super(Duration.class);
    }


}
