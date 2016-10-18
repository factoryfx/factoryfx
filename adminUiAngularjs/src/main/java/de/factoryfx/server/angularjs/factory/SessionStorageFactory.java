package de.factoryfx.server.angularjs.factory;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.LiveCycleController;

public class SessionStorageFactory extends FactoryBase<SessionStorage,Void> {

    @Override
    public LiveCycleController<SessionStorage, Void> createLifecycleController() {
        return () -> new SessionStorage();
    }
}
