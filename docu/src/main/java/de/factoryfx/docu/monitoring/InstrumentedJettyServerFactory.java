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
        configLiveCycle().setCreator(() -> {
            MetricRegistry metricRegistry=new MetricRegistry();
            JettyServer jettyServer = new JettyServer(
                    connectors.instances(),
                    getResourcesInstances(),
                    Collections.singletonList(new InstrumentedHandler(metricRegistry, "monitoring example")),
                    ObjectMapperBuilder.buildNewObjectMapper(),
                    new LoggingFeature(new DelegatingLoggingFilterLogger()),
                    new DefaultResourceConfigSetup()

            );
            return new InstrumentedJettyServer(jettyServer, metricRegistry);


        });
        configLiveCycle().setReCreator(currentLiveObject->currentLiveObject.recreate(connectors.instances(),getResourcesInstances()));

        configLiveCycle().setStarter(InstrumentedJettyServer::start);
        configLiveCycle().setDestroyer(InstrumentedJettyServer::stop);

        config().setDisplayTextProvider(() -> "InstrumentedJettyServerFactory");
        configLiveCycle().setRuntimeQueryExecutor((serverVisitor, jettyServer) -> jettyServer.acceptVisitor(serverVisitor));
    }

    private List<Object> getResourcesInstances() {
        return Collections.singletonList(factoryReferenceAttribute.instance());
    }
}
