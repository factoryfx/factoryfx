package de.factoryfx.factory;

import de.factoryfx.factory.log.FactoryLogEntryEventType;

public abstract class SimpleFactoryBase<L,V> extends FactoryBase<L,V>{

    public abstract L createImpl();

    @Override
    L create(){
        if (creator!=null){
            return creator.get();
        }
        return loggedAction(FactoryLogEntryEventType.CREATE, this::createImpl);
    }

}
