package io.github.factoryfx.factory;

import java.util.function.Supplier;

public abstract class SimpleFactoryBase<L,R extends FactoryBase<?,R>> extends FactoryBase<L, R>{

    protected abstract L createImpl();

    @Override
    L createTemplateMethod(){
        return createImpl();
    }

    @Override
    void setCreator(Supplier<L> creator){
        throw new IllegalStateException("can't set creator for SimpleFactoryBase, use createImpl instead");
    }

}
