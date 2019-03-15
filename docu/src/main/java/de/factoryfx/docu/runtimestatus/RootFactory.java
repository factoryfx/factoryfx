package de.factoryfx.docu.runtimestatus;

import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.factory.FactoryBase;


public class RootFactory extends FactoryBase<Root, RootFactory> {
    public final StringAttribute stringAttribute = new StringAttribute();

    public RootFactory(){
        configLifeCycle().setCreator(() ->  new Root(stringAttribute.get()));
        configLifeCycle().setReCreator(previousLiveObject -> new Root(stringAttribute.get(),previousLiveObject.getCounter()));
    }
}
