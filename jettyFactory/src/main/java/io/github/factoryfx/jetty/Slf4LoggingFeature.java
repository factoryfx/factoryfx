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
}
