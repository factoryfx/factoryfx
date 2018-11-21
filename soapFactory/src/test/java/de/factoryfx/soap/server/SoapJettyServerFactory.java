package de.factoryfx.soap.server;


import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.jetty.*;
import de.factoryfx.soap.SoapHandler;
import de.factoryfx.soap.SoapHandlerFactory;
import de.factoryfx.soap.example.HelloWorld;

import java.util.Collections;
import java.util.List;

public class SoapJettyServerFactory extends JettyServerFactory<Void,SoapJettyServerFactory> {

    public final FactoryReferenceAttribute<SoapHandler, SoapHandlerFactory<HelloWorld,Void,SoapJettyServerFactory>> soapHandler = new FactoryReferenceAttribute<>(null);


    public SoapJettyServerFactory() {
        configLifeCycle().setStarter(JettyServer::start);
        configLifeCycle().setDestroyer(JettyServer::stop);
    }

    @Override
    protected void setupServlets(ServletBuilder servletBuilder) {
        servletBuilder.withServlet("/*",soapHandler.instance());
    }

}
