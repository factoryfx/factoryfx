package de.factoryfx.docu.monitoring;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.jetty9.InstrumentedHandler;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.factory.atrribute.FactoryReferenceListAttribute;
import de.factoryfx.server.rest.server.HttpServerConnectorCreator;
import de.factoryfx.server.rest.server.HttpServerConnectorFactory;
import de.factoryfx.server.rest.server.JettyServer;
import de.factoryfx.server.rest.server.JettyServerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/*
    connect ServerVisitor with InstrumentedJettyServer
 */
public class InstrumentedJettyServerFactory extends FactoryBase<InstrumentedJettyServer,ServerVisitor> {
    public final FactoryReferenceAttribute<SimpleResource,SimpleResourceFactory> factoryReferenceAttribute = new FactoryReferenceAttribute<>();
    public final FactoryReferenceListAttribute<HttpServerConnectorCreator,HttpServerConnectorFactory<ServerVisitor>> connectors = new FactoryReferenceListAttribute<HttpServerConnectorCreator,HttpServerConnectorFactory<ServerVisitor>>().setupUnsafe(HttpServerConnectorFactory.class).labelText("connectors").userNotSelectable();



    public InstrumentedJettyServerFactory(){
        super();
        configLiveCycle().setCreator(() -> {
            MetricRegistry metricRegistry=new MetricRegistry();
            new InstrumentedHandler(metricRegistry,"monitoring example");
            JettyServer jettyServer = new JettyServer(connectors.instances(), Arrays.asList(factoryReferenceAttribute.instance()), Arrays.asList(new InstrumentedHandler(metricRegistry,"monitoring example")));
            return new InstrumentedJettyServer(jettyServer, metricRegistry);


        });
        configLiveCycle().setReCreator(currentLiveObject->currentLiveObject.recreate(connectors.instances(), Arrays.asList(factoryReferenceAttribute.instance())));

        configLiveCycle().setStarter(InstrumentedJettyServer::start);
        configLiveCycle().setDestroyer(InstrumentedJettyServer::stop);

        config().setDisplayTextProvider(() -> "ApplicationServerRestServer");

        configLiveCycle().setRuntimeQueryExecutor((serverVisitor, jettyServer) -> jettyServer.acceptVisitor(serverVisitor));
    }

//    @Override
//    protected List<Object> getResourcesInstances() {
//        return Arrays.asList(factoryReferenceAttribute.instance());
//    }


}
