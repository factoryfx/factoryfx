package de.factoryfx.factory.attribute;

import com.fasterxml.jackson.annotation.JsonCreator;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleIntegerProperty;

public class IntegerAttribute extends ValueAttribute<Number, Property<Number>> {

    public IntegerAttribute(AttributeMetadata<Number> attributeMetadata) {
        super(attributeMetadata, () -> new SimpleIntegerProperty());
    }

    public IntegerAttribute(AttributeMetadata<Number> attributeMetadata, Integer defaultValue) {
        this(attributeMetadata);
        set(defaultValue);
    }

    @JsonCreator
    public IntegerAttribute(Integer value) {
        this((AttributeMetadata<Number>) null);
        set(value);
    }

    @Override
    public Integer get() {
        return (Integer) super.get();
    }
}