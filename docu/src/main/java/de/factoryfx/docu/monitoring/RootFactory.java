package de.factoryfx.docu.monitoring;

import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;

public class RootFactory extends SimpleFactoryBase<Root,ServerVisitor, RootFactory> {
    public final FactoryReferenceAttribute<InstrumentedJettyServer,InstrumentedJettyServerFactory> server=new FactoryReferenceAttribute<>(InstrumentedJettyServerFactory.class).labelText("server");

    @Override
    public Root createImpl() {
        return new Root(server.getDisplayText());
    }
}
