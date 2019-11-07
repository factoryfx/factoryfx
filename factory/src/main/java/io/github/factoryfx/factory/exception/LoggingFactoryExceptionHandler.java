package io.github.factoryfx.factory.exception;

import org.slf4j.LoggerFactory;

import io.github.factoryfx.factory.FactoryBase;

public class LoggingFactoryExceptionHandler<L,R extends FactoryBase<L,R>> implements FactoryExceptionHandler<L,R>{

    private final FactoryExceptionHandler<L,R> delegate;

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(LoggingFactoryExceptionHandler.class);

    public LoggingFactoryExceptionHandler(FactoryExceptionHandler<L,R> delegate) {
        this.delegate = delegate;
    }

    @Override
    public void updateException(Exception e, FactoryBase<?,?> factory, ExceptionResponseAction<L,R> exceptionResponse){
        log(e,factory,"createOrRecreate");
        delegate.updateException(e,factory,exceptionResponse);
    }
    @Override
    public void startException(Exception e, FactoryBase<?,?> factory, ExceptionResponseAction<L,R> exceptionResponse) {
        log(e,factory,"start");
        delegate.startException(e,factory,exceptionResponse);
    }
    @Override
    public void destroyException(Exception e, FactoryBase<?,?> factory, ExceptionResponseAction<L,R> exceptionResponse) {
        log(e,factory,"destroy");
        delegate.destroyException(e,factory,exceptionResponse);
    }

    private void log(Exception e, FactoryBase<?,?> factory,String text) {
        logger.error("\nException during " + text + "for:\n" + factory.internal().debugInfo(), e);
    }
}
