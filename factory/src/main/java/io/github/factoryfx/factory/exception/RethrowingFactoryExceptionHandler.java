package io.github.factoryfx.factory.exception;

import io.github.factoryfx.factory.FactoryBase;

public class RethrowingFactoryExceptionHandler<L,R extends FactoryBase<L,R>> implements FactoryExceptionHandler<L,R>{

    @Override
    public void updateException(Exception e, FactoryBase<?,?> factory, ExceptionResponseAction<L,R> exceptionResponse){
        rethrow(e);
    }
    @Override
    public void startException(Exception e, FactoryBase<?,?> factory, ExceptionResponseAction<L,R> exceptionResponse) {
        rethrow(e);
    }
    @Override
    public void destroyException(Exception e, FactoryBase<?,?> factory, ExceptionResponseAction<L,R> exceptionResponse) {
        rethrow(e);
    }

    private void rethrow(Exception e) {
        if (e instanceof RuntimeException){
            throw (RuntimeException)e;
        }
        throw new RuntimeException(e);
    }

}
