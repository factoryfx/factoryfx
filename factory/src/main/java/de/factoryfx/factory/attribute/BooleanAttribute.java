package de.factoryfx.factory.attribute;

import com.fasterxml.jackson.annotation.JsonCreator;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;

public class BooleanAttribute extends ValueAttribute<Boolean, Property<Boolean>> {

    public BooleanAttribute(AttributeMetadata<Boolean> attributeMetadata) {
        super(attributeMetadata, () -> new SimpleBooleanProperty());
        set(Boolean.FALSE);
    }

    public BooleanAttribute(AttributeMetadata<Boolean> attributeMetadata, Boolean defaultValue) {
        this(attributeMetadata);
        set(defaultValue);
    }

    @JsonCreator
    public BooleanAttribute(Boolean value) {
        this((AttributeMetadata<Boolean>) null);
        set(value);
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
