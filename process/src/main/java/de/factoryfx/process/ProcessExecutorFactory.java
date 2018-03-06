package de.factoryfx.process;

import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;


public class ProcessExecutorFactory<P extends Process,PP extends ProcessParameter,V> extends SimpleFactoryBase<ProcessExecutor<P,PP>,V> {
    public final FactoryReferenceAttribute<ProcessStorage,ProcessStorageFactory<V,PP>> processStorage= new FactoryReferenceAttribute<>();

    @Override
    public ProcessExecutor<P, PP> createImpl() {
        return new ProcessExecutor<>(processStorage.instance());
    }
}
