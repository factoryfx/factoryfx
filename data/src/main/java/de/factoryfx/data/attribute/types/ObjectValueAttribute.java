package de.factoryfx.data.attribute.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import de.factoryfx.data.attribute.ImmutableValueAttribute;
import de.factoryfx.data.jackson.ObjectValueAttributeDeserializer;
import de.factoryfx.data.jackson.ObjectValueAttributeSerializer;

/**
 *special case attribute to pass object from outside in the application.
 *the ObjectValue ist not serialised or merged
 */
@JsonSerialize(using = ObjectValueAttributeSerializer.class)
@JsonDeserialize(using = ObjectValueAttributeDeserializer.class)
public class ObjectValueAttribute<T> extends ImmutableValueAttribute<T,ObjectValueAttribute<T>> {
    @JsonCreator
    ObjectValueAttribute(T value) {
        super(null);
        set(value);
    }

    public ObjectValueAttribute() {
        super(null);
    }


    @Override
    public boolean internal_ignoreForMerging() {
        return true;
    }

}