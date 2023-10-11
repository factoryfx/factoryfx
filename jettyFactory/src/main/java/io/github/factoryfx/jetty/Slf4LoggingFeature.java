package io.github.factoryfx.jetty;

import org.glassfish.jersey.logging.LoggingFeature;

/**
 * jersey use java util for logging this class will bridge to slf4j
 *
 * (preferred over the jultoslf bridge cause no global install required and no loglevel setup)
 */
public class Slf4LoggingFeature extends LoggingFeature {

    public Slf4LoggingFeature(){
       super(new Slf4LoggingFeatureLogger());
    }

    public Slf4LoggingFeature(Slf4LoggingFeatureLogger.JerseyLogLevel jettyLogLevel){
        super(new Slf4LoggingFeatureLogger(jettyLogLevel));
    }

    public Slf4LoggingFeature(Slf4LoggingFeatureLogger.JerseyLogLevel jettyLogLevel, Verbosity verbosity){
        super(new Slf4LoggingFeatureLogger(jettyLogLevel), verbosity);
    }

    public Slf4LoggingFeature(Slf4LoggingFeatureLogger slf4LoggingFeatureLogger){
        super(slf4LoggingFeatureLogger);
    }
}
