package de.factoryfx.factory.atrribute;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.data.attribute.DataReferenceListAttribute;
import de.factoryfx.data.attribute.ReferenceListAttribute;
import de.factoryfx.factory.FactoryBase;

public class FactoryReferenceListAttribute<L,T extends FactoryBase<? extends L,?>> extends  ReferenceListAttribute<T,FactoryReferenceListAttribute<L,T>>{


    public FactoryReferenceListAttribute() {
        super();
    }

    public FactoryReferenceListAttribute(Class<T> clazz) {
        super();
        setup(clazz);
    }

    public List<L> instances(){
        if (get()==null){
            return null;
        }
        ArrayList<L> result = new ArrayList<>();
        for(T item: get()){
            result.add(item.internalFactory().instance());
        }
        return result;
    }

    public boolean add(T data){
        return get().add(data);
    }


    @SuppressWarnings("unchecked")
    public FactoryReferenceListAttribute<L,T> setupUnsafe(Class clazz){
        return setup((Class<T>)clazz);
    }

    public FactoryReferenceListAttribute<L,T> setup(Class<T> clazz){
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
