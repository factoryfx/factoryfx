package de.factoryfx.docu.reuse;

import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.factory.FactoryBase;


public class RootFactory extends FactoryBase<Root,Void, RootFactory> {
    public final StringAttribute stringAttribute = new StringAttribute();

    public RootFactory(){
        configLiveCycle().setCreator(() ->  new Root(new ExpensiveResource()));
        configLiveCycle().setReCreator(previousLiveObject -> new Root(previousLiveObject.getExpensiveResource()));
    }
}
