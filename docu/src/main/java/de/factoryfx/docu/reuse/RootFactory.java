package de.factoryfx.docu.reuse;

import de.factoryfx.factory.FactoryBase;

public class RootFactory extends FactoryBase<Root,Void> {

    public RootFactory(){
//        configLiveCycle().setCreator(() -> new Root());
        configLiveCycle().setStarter(root -> root.start());
        configLiveCycle().setDestroyer(root -> root.destroy());
    }
}
