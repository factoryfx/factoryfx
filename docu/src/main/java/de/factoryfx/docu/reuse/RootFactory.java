package de.factoryfx.docu.reuse;

import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.factory.FactoryBase;


public class RootFactory extends FactoryBase<Root,Void, RootFactory> {
    public final StringAttribute stringAttribute = new StringAttribute();

    public RootFactory(){
        configLifeCycle().setCreator(() ->  new Root(new ExpensiveResource()));
        configLifeCycle().setReCreator(previousLiveObject -> new Root(previousLiveObject.getExpensiveResource()));
    }
}
