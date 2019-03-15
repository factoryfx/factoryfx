package de.factoryfx.factory.exception;

import de.factoryfx.factory.FactoryBase;

/**
 * exit vm for exception, that's the safest action cause factory could used external resources like ports, memory that s not clear able cause the factory create process is an unknown state
 * */
public class AllOrNothingFactoryExceptionHandler<L,R extends FactoryBase<L,R>> implements FactoryExceptionHandler<L,R>{

    @Override
    public void updateException(Exception e, FactoryBase<?,?> factory, ExceptionResponseAction<L,R> exceptionResponse){
        exceptionResponse.terminateApplication();
    }

    @Override
    public void startException(Exception e, FactoryBase<?,?> factory, ExceptionResponseAction<L,R> exceptionResponse) {
        exceptionResponse.terminateApplication();
    }

    @Override
    public void destroyException(Exception e, FactoryBase<?,?> factory, ExceptionResponseAction<L,R> exceptionResponse) {
        exceptionResponse.terminateApplication();
    }
}
