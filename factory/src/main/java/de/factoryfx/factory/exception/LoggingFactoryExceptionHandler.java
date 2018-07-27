package de.factoryfx.factory.exception;

import org.slf4j.LoggerFactory;

import de.factoryfx.factory.FactoryBase;

public class LoggingFactoryExceptionHandler<V,L,R extends FactoryBase<L,V,R>> implements FactoryExceptionHandler<V,L,R>{

    private final FactoryExceptionHandler<V,L,R> delegate;

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(LoggingFactoryExceptionHandler.class);

    public LoggingFactoryExceptionHandler(FactoryExceptionHandler<V,L,R> delegate) {
        this.delegate = delegate;
    }

    @Override
    public void updateException(Exception e, FactoryBase<?,?,?> factory, ExceptionResponseAction<V,L,R> exceptionResponse){
        log(e,factory,"createOrRecreate");
        delegate.updateException(e,factory,exceptionResponse);
    }
    @Override
    public void startException(Exception e, FactoryBase<?,?,?> factory, ExceptionResponseAction<V,L,R> exceptionResponse) {
        log(e,factory,"start");
        delegate.startException(e,factory,exceptionResponse);
    }
    @Override
    public void destroyException(Exception e, FactoryBase<?,?,?> factory, ExceptionResponseAction<V,L,R> exceptionResponse) {
        log(e,factory,"destroy");
        delegate.destroyException(e,factory,exceptionResponse);
    }

    private void log(Exception e, FactoryBase<?,?,?> factory,String text) {
        logger.error("\nException during " + text + " for factory " + factory.getClass() + ":\n" + factory.internalFactory().debugInfo(), e);
    }
}
