package de.factoryfx.factory.atrribute;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.data.attribute.DataReferenceAttribute;
import de.factoryfx.data.attribute.ReferenceAttribute;
import de.factoryfx.factory.FactoryBase;

import java.util.HashSet;
import java.util.Set;

public class FactoryReferenceAttribute<L, T extends FactoryBase<? extends L,?>> extends ReferenceAttribute<T,FactoryReferenceAttribute<L,T>> {


    @JsonCreator
    protected FactoryReferenceAttribute(T value) {
        super(value);
    }
//
//    protected FactoryReferenceAttribute() {
//        super(value);
//    }

    public FactoryReferenceAttribute(Class<T> clazz) {
        super();
        setup(clazz);
    }

    public FactoryReferenceAttribute() {
        super();
    }

    public L instance(){
        if (get()==null){
            return null;
        }
        return get().internalFactory().instance();
    }

    @Override
    public FactoryReferenceAttribute<L,T> internal_copy() {
        return new FactoryReferenceAttribute<>();
    }


    @SuppressWarnings("unchecked")
    public FactoryReferenceAttribute<L,T> setupUnsafe(Class clazz){
        return setup((Class<T>)clazz);
    }

    public FactoryReferenceAttribute<L,T> setup(Class<T> clazz){
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
