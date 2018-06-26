package de.factoryfx.factory.exception;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.RootFactoryWrapper;

/**resret to the previous state after an exception during update */
public class ResettingFactoryExceptionHandler implements FactoryExceptionHandler{

    @Override
    public void updateException(Exception e, FactoryBase<?,?,?> factory, ExceptionResponseAction exceptionResponse){
        exceptionResponse.reset();
    }
    @Override
    public void startException(Exception e, FactoryBase<?,?,?> factory, ExceptionResponseAction exceptionResponse) {
        rethrow(e);
    }
    @Override
    public void destroyException(Exception e, FactoryBase<?,?,?> factory, ExceptionResponseAction exceptionResponse) {
        rethrow(e);
    }

    private void rethrow(Exception e) {
        if (e instanceof RuntimeException){
            throw (RuntimeException)e;
        }
        throw new RuntimeException(e);
    }

}
