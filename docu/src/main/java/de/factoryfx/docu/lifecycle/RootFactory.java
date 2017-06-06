package de.factoryfx.docu.lifecycle;

import de.factoryfx.factory.FactoryBase;

public class RootFactory extends FactoryBase<Root,Void> {

    public RootFactory(){
        configLiveCycle().setCreator(Root::new);
        configLiveCycle().setStarter(Root::start);
        configLiveCycle().setDestroyer(Root::destroy);
    }
}
