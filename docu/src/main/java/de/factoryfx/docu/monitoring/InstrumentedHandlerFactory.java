package de.factoryfx.docu.monitoring;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.jetty9.InstrumentedHandler;
import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import org.eclipse.jetty.server.Handler;

public class InstrumentedHandlerFactory extends SimpleFactoryBase<Handler,RootFactory> {
    public final FactoryReferenceAttribute<MetricRegistry, MetricRegistryFactory> metricRegistry = new FactoryReferenceAttribute<>(MetricRegistryFactory.class);

    @Override
    public Handler createImpl() {
        return new InstrumentedHandler(metricRegistry.instance(), "monitoring example");
    }
}
