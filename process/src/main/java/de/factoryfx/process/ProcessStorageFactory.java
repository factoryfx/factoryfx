package de.factoryfx.process;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.SimpleFactoryBase;

public class ProcessStorageFactory<V,PR extends ProcessParameter, R extends FactoryBase<?,V,R>> extends SimpleFactoryBase<ProcessStorage<PR>,V,R> {

    @Override
    public ProcessStorage<PR> createImpl() {
        return null;
    }
}
