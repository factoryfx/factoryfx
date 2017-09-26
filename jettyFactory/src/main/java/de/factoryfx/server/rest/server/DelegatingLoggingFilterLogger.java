package de.factoryfx.server.rest.server;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.slf4j.LoggerFactory;

/**
 * jersey use java util for logging this class will bridge to slf4j
 *
 * (preferred over the jultoslf bridge cause no global install required, not loglevel setup)
 */
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
