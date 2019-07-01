package io.github.factoryfx.factory.attribute.types;

import com.fasterxml.jackson.annotation.JsonIgnoreType;
import io.github.factoryfx.factory.attribute.AttributeMatch;
import io.github.factoryfx.factory.attribute.ImmutableValueAttribute;

import java.util.Objects;
import java.util.function.Consumer;

/**
 *special case attribute to pass object from outside in the application.
 *the ObjectValue ist not serialised or merged
 */
@JsonIgnoreType
public class ObjectValueAttribute<T> extends ImmutableValueAttribute<T,ObjectValueAttribute<T>> {

    public ObjectValueAttribute() {
        super();
    }

    /**
     * Explanation see: {@link io.github.factoryfx.factory.attribute.dependency.FactoryAttribute#FactoryAttribute(Consumer)}}
     * @param setup setup function
     */
    public ObjectValueAttribute(Consumer<ObjectValueAttribute<T>> setup){
        super();
        setup.accept(this);
    }

    @Override
    public void internal_merge(T newValue) {
        //nothing   ignore for merging
    }

    @Override
    public boolean internal_mergeMatch(AttributeMatch<T> value) {
        return true;//to ignore ObjectValueAttribute during merge
    }

}