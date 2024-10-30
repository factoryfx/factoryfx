package io.github.factoryfx.docu.monitoring;

import com.codahale.metrics.MetricRegistry;

import io.dropwizard.metrics.jetty12.AbstractInstrumentedHandler;
import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;

public class InstrumentedHandlerFactory extends SimpleFactoryBase<Handler,RootFactory> {
    public final FactoryAttribute<MetricRegistry, MetricRegistryFactory> metricRegistry = new FactoryAttribute<>();

    @Override
    protected Handler createImpl() {
        return new AbstractInstrumentedHandler(metricRegistry.instance(), "monitoring example") {
            @Override
            protected void setupServletListeners(Request request, Response response) {

            }

            @Override
            protected boolean isSuspended(Request request, Response response) {
                return false;
            }
        };
    }
}
