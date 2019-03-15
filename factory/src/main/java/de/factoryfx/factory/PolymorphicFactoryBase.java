package de.factoryfx.factory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.lang.reflect.ParameterizedType;
import java.util.function.Supplier;

@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class") //minimal class doesn't work
public abstract class PolymorphicFactoryBase<L,R extends FactoryBase<?,R>> extends FactoryBase<L,R> implements PolymorphicFactory<L>{

    public abstract L createImpl();

    @Override
    L createTemplateMethod(){
        return createImpl();
    }

    @Override
    @SuppressWarnings("unchecked")
    @JsonIgnore
    public Class<L> getLiveObjectClass() {
        return (Class<L>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0];
    }

    @Override
    void setCreator(Supplier<L> creator){
        throw new IllegalStateException("can't set creator for PolymorphicFactoryBase, use createImpl instead");
    }
}
