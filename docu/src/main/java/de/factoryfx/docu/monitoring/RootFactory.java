package de.factoryfx.docu.monitoring;

import com.codahale.metrics.MetricRegistry;
import de.factoryfx.docu.restserver.SimpleHttpServer;
import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.jetty.JettyServerFactory;
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
