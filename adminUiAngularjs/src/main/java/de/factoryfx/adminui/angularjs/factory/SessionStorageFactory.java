package de.factoryfx.adminui.angularjs.factory;

import java.util.Optional;

import de.factoryfx.factory.FactoryBase;

public class SessionStorageFactory extends FactoryBase<SessionStorage,SessionStorageFactory> {
    @Override
    protected SessionStorage createImp(Optional<SessionStorage> previousLiveObject) {
        return new SessionStorage();
    }
}
