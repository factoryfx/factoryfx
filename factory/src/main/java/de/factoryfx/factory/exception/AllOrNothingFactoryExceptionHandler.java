package de.factoryfx.factory.exception;

import de.factoryfx.factory.FactoryBase;

/**
 * exit vm for exception, that's the safest action cause factory could used external resources like ports, memory that s not clear able cause the factory create process is an unknown state
 * */
public class AllOrNothingFactoryExceptionHandler<V,L,R extends FactoryBase<L,V,R>> implements FactoryExceptionHandler<V,L,R>{

    @Override
    public void updateException(Exception e, FactoryBase<?,?,?> factory, ExceptionResponseAction<V,L,R> exceptionResponse){
        exceptionResponse.terminateApplication();
    }

    @Override
    public void startException(Exception e, FactoryBase<?,?,?> factory, ExceptionResponseAction<V,L,R> exceptionResponse) {
        exceptionResponse.terminateApplication();
    }

    @Override
    public void destroyException(Exception e, FactoryBase<?,?,?> factory, ExceptionResponseAction<V,L,R> exceptionResponse) {
        exceptionResponse.terminateApplication();
    }
}
