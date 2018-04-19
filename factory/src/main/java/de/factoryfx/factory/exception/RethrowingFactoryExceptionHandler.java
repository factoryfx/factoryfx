package de.factoryfx.factory.exception;

import de.factoryfx.factory.FactoryBase;

public class RethrowingFactoryExceptionHandler<V, R extends FactoryBase<?,V,R>> implements FactoryExceptionHandler<V,R>{

    @Override
    public void createOrRecreateException(Exception e, FactoryBase<?,V,R> factory, ExceptionResponseAction exceptionResponse){
        rethrow(e);
    }
    @Override
    public void startException(Exception e, FactoryBase<?,V,R> factory, ExceptionResponseAction exceptionResponse) {
        rethrow(e);
    }
    @Override
    public void destroyException(Exception e, FactoryBase<?,V,R> factory, ExceptionResponseAction exceptionResponse) {
        rethrow(e);
    }

    private void rethrow(Exception e) {
        if (e instanceof RuntimeException){
            throw (RuntimeException)e;
        }
        throw new RuntimeException(e);
    }

}
