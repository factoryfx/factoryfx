package io.github.factoryfx.docu.monitoring;

import com.codahale.metrics.MetricRegistry;
import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryReferenceAttribute;
import io.github.factoryfx.jetty.JettyServerFactory;
import org.eclipse.jetty.server.Server;

public class RootFactory  extends SimpleFactoryBase<Root, RootFactory> {

    public final FactoryReferenceAttribute<RootFactory,Server, JettyServerFactory<RootFactory>> server = new FactoryReferenceAttribute<>();
    public final FactoryReferenceAttribute<RootFactory,MetricRegistry, MetricRegistryFactory> metricRegistry = new FactoryReferenceAttribute<>();

    @Override
    public Root createImpl() {
        server.instance();
        return new Root(metricRegistry.instance());
    }
}
