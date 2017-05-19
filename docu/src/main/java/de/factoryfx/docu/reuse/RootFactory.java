package de.factoryfx.docu.reuse;

import de.factoryfx.factory.FactoryBase;

public class RootFactory extends FactoryBase<Root,Void> {

    public RootFactory(){
        configLiveCycle().setCreator(() ->  new Root(new ExpensiveResource()));
        configLiveCycle().setReCreator(previousLiveObject -> new Root(previousLiveObject.getExpensiveResource()));
    }
}
