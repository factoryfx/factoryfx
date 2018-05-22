package de.factoryfx.factory.exception;

import de.factoryfx.factory.FactoryBase;

public class AllOrNothingFactoryExceptionHandler implements FactoryExceptionHandler{

    @Override
    public void createOrRecreateException(Exception e, FactoryBase<?,?,?> factory, ExceptionResponseAction exceptionResponse){
        exceptionResponse.destroyAll();
        exceptionResponse.terminateApplication();
    }

    @Override
    public void startException(Exception e, FactoryBase<?,?,?> factory, ExceptionResponseAction exceptionResponse) {
        exceptionResponse.destroyAll();
        exceptionResponse.terminateApplication();
    }

    @Override
    public void destroyException(Exception e, FactoryBase<?,?,?> factory, ExceptionResponseAction exceptionResponse) {
        exceptionResponse.destroyAll();
        exceptionResponse.terminateApplication();
    }
}
