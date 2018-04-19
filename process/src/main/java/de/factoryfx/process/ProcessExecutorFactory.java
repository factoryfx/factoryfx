package de.factoryfx.process;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;


public class ProcessExecutorFactory<P extends Process,PP extends ProcessParameter,V, R extends FactoryBase<?,V,R>> extends SimpleFactoryBase<ProcessExecutor<P,PP>,V,R> {
    public final FactoryReferenceAttribute<ProcessStorage,ProcessStorageFactory<V,PP,R>> processStorage= new FactoryReferenceAttribute<>();

    @Override
    public ProcessExecutor<P, PP> createImpl() {
        return new ProcessExecutor<P, PP>(processStorage.instance());
    }
}
