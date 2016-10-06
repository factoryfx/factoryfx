package de.factoryfx.adminui.angularjs.factory;

import java.util.Optional;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.LifecycleNotifier;

public class SessionStorageFactory extends FactoryBase<SessionStorage,Void> {
    @Override
    protected SessionStorage createImp(Optional<SessionStorage> previousLiveObject, LifecycleNotifier<Void> lifecycle) {
        return new SessionStorage();
    }
}
