package io.github.factoryfx.factory.attribute.types;

import com.fasterxml.jackson.annotation.JsonIgnoreType;
import io.github.factoryfx.factory.attribute.ImmutableValueAttribute;

/**
 *special case attribute to pass object from outside in the application.
 *the ObjectValue ist not serialised or merged
 */
@JsonIgnoreType
public class ObjectValueAttribute<T> extends ImmutableValueAttribute<T,ObjectValueAttribute<T>> {

    public ObjectValueAttribute() {
        super();
    }


    @Override
    public boolean internal_ignoreForMerging() {
        return true;
    }

}