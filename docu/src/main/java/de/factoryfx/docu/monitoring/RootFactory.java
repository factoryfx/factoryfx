package de.factoryfx.docu.monitoring;

import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.server.rest.server.JettyServer;
import de.factoryfx.server.rest.server.JettyServerFactory;

public class RootFactory extends SimpleFactoryBase<Root,ServerVisitor>{
    public final FactoryReferenceAttribute<InstrumentedJettyServer,InstrumentedJettyServerFactory> server=new FactoryReferenceAttribute<>(InstrumentedJettyServerFactory.class).labelText("server");

    @Override
    public Root createImpl() {
        return new Root(server.getDisplayText());
    }
}
