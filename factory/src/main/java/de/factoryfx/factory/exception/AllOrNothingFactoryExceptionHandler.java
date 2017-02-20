package de.factoryfx.factory.exception;

import de.factoryfx.factory.FactoryBase;

public class AllOrNothingFactoryExceptionHandler<V> implements FactoryExceptionHandler<V>{

    @Override
    public void createOrRecreateException(Exception e, FactoryBase<?,V> factory, ExceptionResponseAction exceptionResponse){
        exceptionResponse.destroyAll();
        exceptionResponse.terminateApplication();
    }

    @Override
    public void startException(Exception e, FactoryBase<?,V> factory, ExceptionResponseAction exceptionResponse) {
        exceptionResponse.destroyAll();
        exceptionResponse.terminateApplication();
    }

    @Override
    public void destroyException(Exception e, FactoryBase<?,V> factory, ExceptionResponseAction exceptionResponse) {
        exceptionResponse.destroyAll();
        exceptionResponse.terminateApplication();
    }
}
