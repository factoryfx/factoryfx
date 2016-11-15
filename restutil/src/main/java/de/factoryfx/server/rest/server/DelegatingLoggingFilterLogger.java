package de.factoryfx.server.rest.server;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.slf4j.LoggerFactory;

//extending from logger is a workaround cause LoggingFilter is final
public class DelegatingLoggingFilterLogger extends Logger {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(DelegatingLoggingFilterLogger.class);

    public DelegatingLoggingFilterLogger() {
        super("dummy", null);
    }

    //configuration is broken in jersey cause obsession with key value
    @Override
    public boolean isLoggable(Level level){
        return true;
    }

    @Override
    public void info(String msg) {
        logger.info(msg);
    }

    @Override
    public void log(Level level, String msg) {
        logger.info(msg);
    }
}
