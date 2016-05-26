package de.factoryfx.factory;

import java.util.Map;
import java.util.Optional;

public class PreviousLiveObjectProvider {

    private final Map<String,LiveObject> previousLifeObjects;

    public PreviousLiveObjectProvider(Map<String, LiveObject> previousLifeObjects) {
        this.previousLifeObjects = previousLifeObjects;
    }

    public <T extends LiveObject> Optional<T> get(FactoryBase<T,?> factoryBase){
        LiveObject liveObject = previousLifeObjects.get(factoryBase);
        return Optional.ofNullable((T) liveObject);

    }
}
