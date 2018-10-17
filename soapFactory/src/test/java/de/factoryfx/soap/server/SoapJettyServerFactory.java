package de.factoryfx.soap.server;


import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.factory.atrribute.FactoryReferenceListAttribute;
import de.factoryfx.jetty.*;
import de.factoryfx.soap.SoapHandler;
import de.factoryfx.soap.SoapHandlerFactory;
import de.factoryfx.soap.example.HelloWorld;

import java.util.Collections;
import java.util.List;

public class SoapJettyServerFactory extends JettyServerFactory<Void,SoapJettyServerFactory> {

    public final FactoryReferenceAttribute<SoapHandler<HelloWorld>, SoapHandlerFactory<HelloWorld,Void,SoapJettyServerFactory>> soapHandler = new FactoryReferenceAttribute<>(null);


    public SoapJettyServerFactory() {
        configLiveCycle().setStarter(JettyServer::start);
        configLiveCycle().setDestroyer(JettyServer::stop);
    }

    @Override
    protected List<Object> getResourcesInstances() {
        return Collections.emptyList();
    }

    @Override
    protected List<BasicRequestHandler> getBasicRequestHandlerInstances() {
        return List.of(soapHandler.instance());
    }
}
