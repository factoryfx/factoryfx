package de.factoryfx.docu.dependency;

import de.factoryfx.factory.SimpleFactoryBase;

public class Dependency extends SimpleFactoryBase<Dependency,Void>{

    @Override
    public Dependency createImpl() {
        return new Dependency();
    }

    public void doX(){
        System.out.println("doX");
    }
}
