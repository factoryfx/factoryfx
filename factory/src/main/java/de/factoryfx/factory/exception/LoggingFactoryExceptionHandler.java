package de.factoryfx.factory.exception;

import de.factoryfx.factory.FactoryBase;
import org.slf4j.LoggerFactory;

public class LoggingFactoryExceptionHandler implements FactoryExceptionHandler{

    private final FactoryExceptionHandler delegate;

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(LoggingFactoryExceptionHandler.class);

    public LoggingFactoryExceptionHandler(FactoryExceptionHandler delegate) {
        this.delegate = delegate;
    }

    @Override
    public void createOrRecreateException(Exception e, FactoryBase<?,?,?> factory, ExceptionResponseAction exceptionResponse){
        log(e,factory,"createOrRecreate");
        delegate.createOrRecreateException(e,factory,exceptionResponse);
    }
    @Override
    public void startException(Exception e, FactoryBase<?,?,?> factory, ExceptionResponseAction exceptionResponse) {
        log(e,factory,"start");
        delegate.startException(e,factory,exceptionResponse);
    }
    @Override
    public void destroyException(Exception e, FactoryBase<?,?,?> factory, ExceptionResponseAction exceptionResponse) {
        log(e,factory,"destroy");
        delegate.destroyException(e,factory,exceptionResponse);
    }

    private void log(Exception e, FactoryBase<?,?,?> factory,String text) {
        logger.error("\nException during "+text+" for factory:\n"+factory.internalFactory().debugInfo(), e);
    }
}
