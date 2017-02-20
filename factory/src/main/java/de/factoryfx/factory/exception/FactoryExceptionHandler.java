package de.factoryfx.factory.exception;

import de.factoryfx.factory.FactoryBase;

public interface FactoryExceptionHandler<V> {

    void createOrRecreateException(Exception e, FactoryBase<?,V> factory, ExceptionResponseAction exceptionResponse);
    void startException(Exception e, FactoryBase<?,V> factory, ExceptionResponseAction exceptionResponse);
    void destroyException(Exception e, FactoryBase<?,V> factory, ExceptionResponseAction exceptionResponse);

}
