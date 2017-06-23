package de.factoryfx.data.attribute;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.data.Data;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public class DataReferenceAttribute<T extends Data> extends ReferenceAttribute<T,DataReferenceAttribute<T>> {


    @JsonCreator
    protected DataReferenceAttribute(T value) {
        super(value);
    }

    public DataReferenceAttribute(Class<T> clazz) {
        super();
        setup(clazz);
    }

    public DataReferenceAttribute<T> setup(Class<T> clazz){
        this.possibleValueProvider(data -> {
            Set<T> result = new HashSet<>();
            for (Data factory: root.internal().collectChildrenDeep()){
                if (clazz.isAssignableFrom(factory.getClass())){
                    result.add((T) factory);
                }
            }
            return result;
        });
        this.newValueProvider(data -> {
            try {
                return clazz.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
        return this;
    }


    public DataReferenceAttribute() {
        super();
    }



}
