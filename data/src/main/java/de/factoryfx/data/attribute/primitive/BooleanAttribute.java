package de.factoryfx.data.attribute.primitive;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.data.attribute.ImmutableValueAttribute;

import java.util.function.Function;

public class BooleanAttribute extends ImmutableValueAttribute<Boolean,BooleanAttribute> {

    public BooleanAttribute() {
        super(Boolean.class);
        set(Boolean.FALSE);
    }

    @JsonCreator
    BooleanAttribute(Boolean value) {
        super(Boolean.class);
        set(value);
    }

    @Override
    protected BooleanAttribute createNewEmptyInstance() {
        return new BooleanAttribute();
    }

    @Override
    public Boolean get() {
        Boolean r = super.get();
        if (r == null) {
            return Boolean.FALSE;
        }
        return r;
    }

}
