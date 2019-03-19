package io.github.factoryfx.factory.exception;

import io.github.factoryfx.factory.FactoryBase;

public interface FactoryExceptionHandler<L,R extends FactoryBase<L,R>> {

    void updateException(Exception e, FactoryBase<?,?> factory, ExceptionResponseAction<L,R> exceptionResponse);
    void startException(Exception e, FactoryBase<?,?> factory, ExceptionResponseAction<L,R> exceptionResponse);
    void destroyException(Exception e, FactoryBase<?,?> factory, ExceptionResponseAction<L,R> exceptionResponse);

}
