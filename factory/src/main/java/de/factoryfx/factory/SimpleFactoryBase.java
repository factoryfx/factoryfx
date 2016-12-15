package de.factoryfx.factory;

public abstract class SimpleFactoryBase<L,V> extends FactoryBase<L,V>{

    public abstract L createImpl();

    @Override
    L create(){
        if (creator!=null){
            return creator.get();
        }
        return createImpl();
    }

}
