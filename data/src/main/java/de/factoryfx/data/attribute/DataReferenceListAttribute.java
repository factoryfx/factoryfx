package de.factoryfx.data.attribute;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.data.Data;

import java.util.*;
import java.util.function.Function;

public class DataReferenceListAttribute<T extends Data> extends ReferenceListAttribute<T,DataReferenceListAttribute<T>> {

    public DataReferenceListAttribute() {
        super();
    }

    public DataReferenceListAttribute(Class<T> clazz) {
        super();
        setup(clazz);
    }


    public DataReferenceListAttribute<T> setup(Class<T> clazz){
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

}
