package de.factoryfx.docu.lifecycle;

import de.factoryfx.factory.FactoryBase;

public class RootFactory extends FactoryBase<Root,Void, RootFactory> {

    public RootFactory(){
        configLiveCycle().setCreator(Root::new);
        configLiveCycle().setReCreator(oldRoot -> new Root());
        configLiveCycle().setStarter(newRoot -> newRoot.start());
        configLiveCycle().setDestroyer(oldRoot -> oldRoot.destroy());
    }
}
