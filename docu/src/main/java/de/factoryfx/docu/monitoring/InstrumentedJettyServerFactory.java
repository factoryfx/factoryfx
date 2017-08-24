package de.factoryfx.docu.monitoring;

import de.factoryfx.server.rest.server.JettyServerFactory;

/*
    connect ServerVisitor with InstrumentedJettyServer
 */
public class InstrumentedJettyServerFactory extends JettyServerFactory<ServerVisitor> {
    public InstrumentedJettyServerFactory(){
        super();
        configLiveCycle().setCreator(() -> new InstrumentedJettyServer(connectors.instances(), resources.instances()));
        configLiveCycle().setRuntimeQueryExecutor((serverVisitor, jettyServer) -> ((InstrumentedJettyServer)jettyServer).acceptVisitor(serverVisitor));
    }

}
