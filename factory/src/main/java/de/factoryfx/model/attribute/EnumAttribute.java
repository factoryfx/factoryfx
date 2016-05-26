package de.factoryfx.model.attribute;

import com.fasterxml.jackson.annotation.JsonCreator;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;

public class EnumAttribute<T extends Enum<T>> extends ValueAttribute<T, Property<T>> {

    public EnumAttribute(AttributeMetadata<T> attributeMetadata) {
        super(attributeMetadata, () -> new SimpleObjectProperty<>());
    }

    public EnumAttribute(AttributeMetadata<T> attributeMetadata, T value) {
        super(attributeMetadata, () -> new SimpleObjectProperty<>());
        set(value);
    }

    @JsonCreator
    public EnumAttribute(T value) {
        this((AttributeMetadata<T>) null);
        set(value);
    }
}
