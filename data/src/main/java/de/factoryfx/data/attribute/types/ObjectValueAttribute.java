package de.factoryfx.data.attribute.types;

import com.fasterxml.jackson.annotation.JsonIgnoreType;
import de.factoryfx.data.attribute.ImmutableValueAttribute;

/**
 *special case attribute to pass object from outside in the application.
 *the ObjectValue ist not serialised or merged
 */
@JsonIgnoreType
public class ObjectValueAttribute<T> extends ImmutableValueAttribute<T,ObjectValueAttribute<T>> {

    public ObjectValueAttribute() {
        super(null);
    }


    @Override
    public boolean internal_ignoreForMerging() {
        return true;
    }

}