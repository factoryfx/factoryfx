package de.factoryfx.docu.monitoring;

import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.server.rest.server.JettyServerFactory;

import java.util.Arrays;
import java.util.List;

/*
    connect ServerVisitor with InstrumentedJettyServer
 */
public class InstrumentedJettyServerFactory extends JettyServerFactory<ServerVisitor> {
    public final FactoryReferenceAttribute<SimpleResource,SimpleResourceFactory> factoryReferenceAttribute = new FactoryReferenceAttribute<>();

    public InstrumentedJettyServerFactory(){
        super();
        configLiveCycle().setCreator(() -> new InstrumentedJettyServer(connectors.instances(), getResourcesInstances()));
        configLiveCycle().setRuntimeQueryExecutor((serverVisitor, jettyServer) -> ((InstrumentedJettyServer)jettyServer).acceptVisitor(serverVisitor));
    }

    @Override
    protected List<Object> getResourcesInstances() {
        return Arrays.asList(factoryReferenceAttribute.instance());
    }


}
