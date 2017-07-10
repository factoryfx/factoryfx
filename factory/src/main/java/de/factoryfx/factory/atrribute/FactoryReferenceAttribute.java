package de.factoryfx.factory.atrribute;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.ReferenceAttribute;
import de.factoryfx.factory.FactoryBase;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public class FactoryReferenceAttribute<L, T extends FactoryBase<? extends L,?>> extends ReferenceAttribute<T,FactoryReferenceAttribute<L,T>> {

    @JsonCreator
    protected FactoryReferenceAttribute(T value) {
        super(value);
    }

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

    @SuppressWarnings("unchecked")
    public FactoryReferenceAttribute<L,T> setupUnsafe(Class clazz){
        return setup((Class<T>)clazz);
    }

    public FactoryReferenceAttribute<L,T> setup(Class<T> clazz){
        this.possibleValueProvider(new DefaultPossibleValueProvider<>(clazz));
        this.newValueProvider(new DefaultNewValueProvider<>(clazz));
        return this;
    }

}
