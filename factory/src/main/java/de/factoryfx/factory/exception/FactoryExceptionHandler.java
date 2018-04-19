package de.factoryfx.factory.exception;

import de.factoryfx.factory.FactoryBase;

public interface FactoryExceptionHandler<V,R extends FactoryBase<?,V,R>> {

    void createOrRecreateException(Exception e, FactoryBase<?,V,R> factory, ExceptionResponseAction exceptionResponse);
    void startException(Exception e, FactoryBase<?,V,R> factory, ExceptionResponseAction exceptionResponse);
    void destroyException(Exception e, FactoryBase<?,V,R> factory, ExceptionResponseAction exceptionResponse);

}
