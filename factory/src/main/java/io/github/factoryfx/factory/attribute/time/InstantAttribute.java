package io.github.factoryfx.factory.attribute.time;

import io.github.factoryfx.factory.attribute.ImmutableValueAttribute;
import java.time.Instant;

public class InstantAttribute extends ImmutableValueAttribute<Instant,InstantAttribute> {

    public InstantAttribute() {
        super();
    }

//    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
//    @Override
//    protected Instant getValue() {
//        return super.getValue();
//    }
//
//    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
//    @Override
//    protected void setValue(Instant value) {
//        super.setValue(value);
//    }
}
