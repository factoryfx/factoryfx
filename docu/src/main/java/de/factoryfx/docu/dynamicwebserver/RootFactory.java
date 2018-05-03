package de.factoryfx.docu.dynamicwebserver;

import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.jetty.JettyServer;

public class RootFactory extends SimpleFactoryBase<JettyServer,Void,RootFactory> {
    public final FactoryReferenceAttribute<JettyServer,Main.DynamicWebserver> server=new FactoryReferenceAttribute<>(Main.DynamicWebserver.class).labelText("server");

    @Override
    public JettyServer createImpl() {
        return server.instance();
    }
}
