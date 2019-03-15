package de.factoryfx.jetty;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.SimpleFactoryBase;
import org.glassfish.jersey.logging.LoggingFeature;

/**
 * jersey use java util for logging this class will bridge to slf4j
 *
 * (preferred over the jultoslf bridge cause no global install required and no loglevel setup)
 */
public class Slf4LoggingFeatureFactory<V,R extends FactoryBase<?,R>> extends SimpleFactoryBase<LoggingFeature,R> {

    @Override
    public LoggingFeature createImpl() {
        return new org.glassfish.jersey.logging.LoggingFeature(new Slf4LoggingFeature());
    }
}
