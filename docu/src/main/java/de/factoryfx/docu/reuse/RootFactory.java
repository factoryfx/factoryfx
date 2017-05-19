package de.factoryfx.docu.reuse;

import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.factory.FactoryBase;

public class RootFactory extends FactoryBase<Root,Void> {
    public final StringAttribute stringAttribute = new StringAttribute(new AttributeMetadata());

    public RootFactory(){
        configLiveCycle().setCreator(() ->  new Root(new ExpensiveResource()));
        configLiveCycle().setReCreator(previousLiveObject -> new Root(previousLiveObject.getExpensiveResource()));
    }
}
