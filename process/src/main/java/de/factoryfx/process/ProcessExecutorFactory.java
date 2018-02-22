package de.factoryfx.process;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import de.factoryfx.data.Data;
import de.factoryfx.factory.SimpleFactoryBase;


public abstract class ProcessExecutorFactory<P extends Process,PR,V> extends SimpleFactoryBase<ProcessExecutor<P,PR>,V> {

    private P process;

    public ProcessExecutorFactory() {

    }

    abstract protected P create();




}
