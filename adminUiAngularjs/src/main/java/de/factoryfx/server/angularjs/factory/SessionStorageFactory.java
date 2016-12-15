package de.factoryfx.server.angularjs.factory;

import de.factoryfx.factory.SimpleFactoryBase;

public class SessionStorageFactory extends SimpleFactoryBase<SessionStorage,Void> {

    @Override
    public SessionStorage createImpl() {
        return new SessionStorage();
    }

}
