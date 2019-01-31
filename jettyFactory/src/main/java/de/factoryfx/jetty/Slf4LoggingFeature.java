package de.factoryfx.jetty;

import org.slf4j.LoggerFactory;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * jersey use java util for logging this class will bridge to slf4j
 *
 * (preferred over the jultoslf bridge cause no global install required and no loglevel setup)
 */
public class Slf4LoggingFeature extends Logger {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(Slf4LoggingFeature.class);

    public Slf4LoggingFeature() {
        super("dummy", null);
    }

    //configuration is broken in jersey cause obsession with key value
    @Override
    public boolean isLoggable(Level level){
        return true;
    }

    @Override
    public void info(String msg) {
        log(msg);
    }

    @Override
    public void log(Level level, String msg) {
        log(msg);
    }

    protected void log(String msg) {
        logger.info(msg);
    }
}
