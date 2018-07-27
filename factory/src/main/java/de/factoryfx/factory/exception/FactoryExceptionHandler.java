package de.factoryfx.factory.exception;

import de.factoryfx.factory.FactoryBase;

public interface FactoryExceptionHandler<V,L,R extends FactoryBase<L,V,R>> {

    void updateException(Exception e, FactoryBase<?,?,?> factory, ExceptionResponseAction<V,L,R> exceptionResponse);
    void startException(Exception e, FactoryBase<?,?,?> factory, ExceptionResponseAction<V,L,R> exceptionResponse);
    void destroyException(Exception e, FactoryBase<?,?,?> factory, ExceptionResponseAction<V,L,R> exceptionResponse);

}
