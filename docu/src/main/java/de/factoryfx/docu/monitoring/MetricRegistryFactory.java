package de.factoryfx.docu.monitoring;

import com.codahale.metrics.MetricRegistry;
import de.factoryfx.factory.SimpleFactoryBase;

public class MetricRegistryFactory extends SimpleFactoryBase<MetricRegistry, RootFactory> {
    @Override
    public MetricRegistry createImpl() {
        return new MetricRegistry();
    }

    MetricRegistryFactory(){

    }
}
