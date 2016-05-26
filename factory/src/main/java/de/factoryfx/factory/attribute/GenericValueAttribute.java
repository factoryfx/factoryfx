package de.factoryfx.factory.attribute;

import com.fasterxml.jackson.annotation.JsonCreator;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;

public class GenericValueAttribute<T> extends ValueAttribute<T, Property<T>> {
    public GenericValueAttribute(AttributeMetadata<T> attributeMetadata) {
        super(attributeMetadata, () -> new SimpleObjectProperty<>());
    }

    public GenericValueAttribute(AttributeMetadata<T> attributeMetadata, T defaultValue) {
        this(attributeMetadata);
        set(defaultValue);
    }

    @JsonCreator
    public GenericValueAttribute(T value) {
        this((AttributeMetadata<T>) null);
        set(value);
    }
}
