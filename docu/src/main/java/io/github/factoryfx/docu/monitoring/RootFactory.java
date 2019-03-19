package io.github.factoryfx.docu.monitoring;

import com.codahale.metrics.MetricRegistry;
import io.github.factoryfx.docu.restserver.SimpleHttpServer;
import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import io.github.factoryfx.jetty.JettyServerFactory;
import org.eclipse.jetty.server.Server;

public class RootFactory  extends SimpleFactoryBase<Root, RootFactory> {
    @SuppressWarnings("unchecked")
    public final FactoryReferenceAttribute<Server, JettyServerFactory<SimpleHttpServer>> server = FactoryReferenceAttribute.create(new FactoryReferenceAttribute<>(JettyServerFactory.class));
    public final FactoryReferenceAttribute<MetricRegistry, MetricRegistryFactory> metricRegistry = new FactoryReferenceAttribute<>(MetricRegistryFactory.class);

    @Override
    public Root createImpl() {
        server.instance();
        return new Root(metricRegistry.instance());
    }
}
