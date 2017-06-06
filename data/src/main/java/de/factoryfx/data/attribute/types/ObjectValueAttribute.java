package de.factoryfx.data.attribute.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.ImmutableValueAttribute;
import de.factoryfx.data.jackson.ObjectValueAttributeDeserializer;
import de.factoryfx.data.jackson.ObjectValueAttributeSerializer;

/**
 *special case attribute to pass object from outside in the application.
 *the ObjectValue ist not serialised or merged
 */
@JsonSerialize(using = ObjectValueAttributeSerializer.class)
@JsonDeserialize(using = ObjectValueAttributeDeserializer.class)
public class ObjectValueAttribute<T> extends ImmutableValueAttribute<T> {
    @JsonCreator
    ObjectValueAttribute(T value) {
        super(null,null);
        set(value);
    }

    public ObjectValueAttribute(AttributeMetadata attributeMetadata) {
        super(attributeMetadata,null);
    }

    @Override
    protected Attribute<T> createNewEmptyInstance() {
        return new ObjectValueAttribute<>(metadata);
    }

    @Override
    public boolean ignoreForMerging() {
        return true;
    }

}