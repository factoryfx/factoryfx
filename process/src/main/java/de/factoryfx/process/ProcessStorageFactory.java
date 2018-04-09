package de.factoryfx.process;

import de.factoryfx.factory.SimpleFactoryBase;

public class ProcessStorageFactory<V,PR extends ProcessParameter> extends SimpleFactoryBase<ProcessStorage<PR>,V> {

    @Override
    public ProcessStorage<PR> createImpl() {
        return null;
    }
}
