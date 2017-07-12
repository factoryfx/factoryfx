package de.factoryfx.data.attribute.primitive.list;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.data.attribute.ValueListAttribute;

public class FloatListAttribute extends ValueListAttribute<Float,FloatListAttribute> {
    @JsonCreator
    public FloatListAttribute() {
        super(Float.class);
    }

}