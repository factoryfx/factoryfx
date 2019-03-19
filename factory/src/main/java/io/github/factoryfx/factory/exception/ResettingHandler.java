package io.github.factoryfx.factory.exception;

import io.github.factoryfx.factory.FactoryBase;

/**
 * reset to the previous state after an exception during update
 * (Some memory/resource leaks can't be fixed in the framework e.g if the factory close code throw an exception again or is wrong implemented)
 * */
public class ResettingHandler<L,R extends FactoryBase<L,R>> implements FactoryExceptionHandler<L,R>{



    @Override
    public void updateException(Exception e, FactoryBase<?,?> factory, ExceptionResponseAction<L,R> exceptionResponse){
        exceptionResponse.reset();
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
