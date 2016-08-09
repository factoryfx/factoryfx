package de.factoryfx.factory.attribute.util;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import de.factoryfx.factory.attribute.AttributeMetadata;
import de.factoryfx.factory.attribute.ValueAttribute;
import de.factoryfx.factory.jackson.ObjectValueAttributeDeserializer;
import de.factoryfx.factory.jackson.ObjectValueAttributeSerializer;
import de.factoryfx.factory.merge.attribute.AttributeMergeHelper;
import de.factoryfx.factory.merge.attribute.ObjectValueMergeHelper;

@JsonSerialize(using = ObjectValueAttributeSerializer.class)
@JsonDeserialize(using = ObjectValueAttributeDeserializer.class)
public class ObjectValueAttribute<T> extends ValueAttribute<T,ObjectValueAttribute<T>> {
    @JsonCreator
    ObjectValueAttribute(T value) {
        super(null,null);
        set(value);
    }

    public ObjectValueAttribute(AttributeMetadata attributeMetadata) {
        super(attributeMetadata,null);
    }

    @Override
    public AttributeMergeHelper<?> createMergeHelper() {
        return new ObjectValueMergeHelper<>(this);
    }

}