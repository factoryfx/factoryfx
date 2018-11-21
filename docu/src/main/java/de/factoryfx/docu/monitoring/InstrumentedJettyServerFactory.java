package de.factoryfx.docu.monitoring;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.jetty9.InstrumentedHandler;
import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.factory.atrribute.FactoryReferenceListAttribute;
import de.factoryfx.jetty.*;
import org.glassfish.jersey.logging.LoggingFeature;

import java.util.Collections;
import java.util.List;

/*
    connect ServerVisitor with InstrumentedJettyServer
 */
public class InstrumentedJettyServerFactory extends FactoryBase<InstrumentedJettyServer,ServerVisitor,RootFactory> {
    public final FactoryReferenceAttribute<SimpleResource,SimpleResourceFactory> factoryReferenceAttribute = new FactoryReferenceAttribute<>(SimpleResourceFactory.class);
    @SuppressWarnings("unchecked")
    public final FactoryReferenceListAttribute<HttpServerConnectorCreator,HttpServerConnectorFactory<ServerVisitor,RootFactory>> connectors = FactoryReferenceListAttribute.create(new FactoryReferenceListAttribute<>(HttpServerConnectorFactory.class).labelText("connectors").userNotSelectable());

    public InstrumentedJettyServerFactory(){
        super();
        configLifeCycle().setCreator(() -> {
            MetricRegistry metricRegistry=new MetricRegistry();
            ServletBuilder servletBuilder = createServletBuilder();
            JettyServer jettyServer = new JettyServer(
                    connectors.instances(),
                    servletBuilder,
                    Collections.singletonList(new InstrumentedHandler(metricRegistry, "monitoring example")),
                    new LoggingFeature(new DelegatingLoggingFilterLogger()),
                    new DefaultResourceConfigSetup()

            );
            return new InstrumentedJettyServer(jettyServer, metricRegistry);


        });
        configLifeCycle().setReCreator(currentLiveObject->currentLiveObject.recreate(connectors.instances(),createServletBuilder()));

        configLifeCycle().setStarter(InstrumentedJettyServer::start);
        configLifeCycle().setDestroyer(InstrumentedJettyServer::stop);

        config().setDisplayTextProvider(() -> "InstrumentedJettyServerFactory");
        configLifeCycle().setRuntimeQueryExecutor((serverVisitor, jettyServer) -> jettyServer.acceptVisitor(serverVisitor));
    }

    private ServletBuilder createServletBuilder() {
        ServletBuilder servletBuilder = new ServletBuilder();
        servletBuilder.withJerseyResources("/*",getResourcesInstances());
        return servletBuilder;
    }

    private List<Object> getResourcesInstances() {
        return Collections.singletonList(factoryReferenceAttribute.instance());
    }
}
