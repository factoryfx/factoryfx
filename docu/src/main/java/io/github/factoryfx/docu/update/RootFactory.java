package io.github.factoryfx.docu.update;

import io.github.factoryfx.data.attribute.types.StringAttribute;
import io.github.factoryfx.factory.FactoryBase;


public class RootFactory extends FactoryBase<Root, RootFactory> {
    public final StringAttribute stringAttribute = new StringAttribute();

    public RootFactory(){
        configLifeCycle().setCreator(() ->  new Root(stringAttribute.get()));
        configLifeCycle().setStarter(root -> {
            //artificial long start time
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }
}
