package de.factoryfx.data.attribute.primitive;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.ImmutableValueAttribute;

public class BooleanAttribute extends ImmutableValueAttribute<Boolean> {

    public BooleanAttribute(AttributeMetadata attributeMetadata) {
        super(attributeMetadata,Boolean.class);
        set(Boolean.FALSE);
    }

    @JsonCreator
    BooleanAttribute(Boolean value) {
        super(null,Boolean.class);
        set(value);
    }

    @Override
    protected Attribute<Boolean> createNewEmptyInstance() {
        return new BooleanAttribute(metadata);
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
