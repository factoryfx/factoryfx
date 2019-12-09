package io.github.factoryfx.jetty;

import org.slf4j.LoggerFactory;

import java.util.function.BiConsumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * jersey use java util for logging this class will bridge to slf4j
 *
 * (preferred over the jultoslf bridge cause no global install required and no loglevel setup)
 */
public class Slf4LoggingFeatureLogger extends Logger {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(Slf4LoggingFeatureLogger.class);
    private final BiConsumer<org.slf4j.Logger, String> loggerStringBiConsumer;

    public enum JerseyLogLevel {
        ERROR(org.slf4j.Logger::error),
        WARN(org.slf4j.Logger::warn),
        DEBUG(org.slf4j.Logger::debug),
        INFO(org.slf4j.Logger::info),
        TRACE(org.slf4j.Logger::trace);

        final BiConsumer<org.slf4j.Logger, String> logConsumer;

        JerseyLogLevel(BiConsumer<org.slf4j.Logger, String> logConsumer) {this.logConsumer = logConsumer;}
    }

    public Slf4LoggingFeatureLogger() {
        this(JerseyLogLevel.INFO);
    }

    public Slf4LoggingFeatureLogger(JerseyLogLevel logLevel) {
        super("dummy", null);
        this.loggerStringBiConsumer = logLevel.logConsumer;
    }

    //configuration is broken in jersey cause obsession with key value
    @Override
    public boolean isLoggable(Level level) {
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
        loggerStringBiConsumer.accept(logger, msg);
    }
}
