package io.github.factoryfx.docu.monitoring;

import com.codahale.metrics.MetricRegistry;
import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;
import io.github.factoryfx.jetty.JettyServerFactory;
import org.eclipse.jetty.server.Server;

public class RootFactory  extends SimpleFactoryBase<Root, RootFactory> {

    public final FactoryAttribute<RootFactory,Server, JettyServerFactory<RootFactory>> server = new FactoryAttribute<>();
    public final FactoryAttribute<RootFactory,MetricRegistry, MetricRegistryFactory> metricRegistry = new FactoryAttribute<>();

    @Override
    public Root createImpl() {
        server.instance();
        return new Root(metricRegistry.instance());
    }
}
