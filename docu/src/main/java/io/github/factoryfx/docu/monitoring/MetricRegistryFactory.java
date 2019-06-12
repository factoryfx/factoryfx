package io.github.factoryfx.docu.monitoring;

import com.codahale.metrics.MetricRegistry;
import io.github.factoryfx.factory.SimpleFactoryBase;

public class MetricRegistryFactory extends SimpleFactoryBase<MetricRegistry, RootFactory> {
    @Override
    protected MetricRegistry createImpl() {
        return new MetricRegistry();
    }

    MetricRegistryFactory(){

    }
}
