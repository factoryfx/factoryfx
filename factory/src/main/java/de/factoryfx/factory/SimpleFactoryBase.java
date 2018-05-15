package de.factoryfx.factory;

import de.factoryfx.factory.log.FactoryLogEntryEventType;

public abstract class SimpleFactoryBase<L,V,R extends FactoryBase<?,V,R>> extends FactoryBase<L,V, R>{

    public abstract L createImpl();

    @Override
    L create(){
        if (creator!=null){
            return creator.get();
        }
        return loggedAction(FactoryLogEntryEventType.CREATE, this::createImpl);
    }

}
