package de.factoryfx.factory;

import java.util.function.Supplier;

public abstract class SimpleFactoryBase<L,V,R extends FactoryBase<?,V,R>> extends FactoryBase<L,V, R>{

    public abstract L createImpl();

    @Override
    L createTemplateMethod(){
        return createImpl();
    }

    @Override
    void setCreator(Supplier<L> creator){
        throw new IllegalStateException("can't set creator for SimpleFactoryBase, use createImpl instead");
    }

}
