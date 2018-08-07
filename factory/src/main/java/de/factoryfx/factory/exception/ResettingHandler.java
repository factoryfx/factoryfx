package de.factoryfx.factory.exception;

import de.factoryfx.factory.FactoryBase;

/**reset to the previous state after an exception during update */
public class ResettingHandler<V,L,R extends FactoryBase<L,V,R>> implements FactoryExceptionHandler<V,L,R>{



    @Override
    public void updateException(Exception e, FactoryBase<?,?,?> factory, ExceptionResponseAction<V,L,R> exceptionResponse){
        exceptionResponse.reset();
    }
    @Override
    public void startException(Exception e, FactoryBase<?,?,?> factory, ExceptionResponseAction<V,L,R> exceptionResponse) {
        rethrow(e);
    }
    @Override
    public void destroyException(Exception e, FactoryBase<?,?,?> factory, ExceptionResponseAction<V,L,R> exceptionResponse) {
        rethrow(e);
    }

    private void rethrow(Exception e) {
        if (e instanceof RuntimeException){
            throw (RuntimeException)e;
        }
        throw new RuntimeException(e);
    }

}
