package de.factoryfx.soap.server;


import de.factoryfx.soap.example.HelloWorld;
import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.factory.atrribute.FactoryReferenceListAttribute;
import de.factoryfx.jetty.*;
import de.factoryfx.soap.SoapHandler;
import de.factoryfx.soap.SoapHandlerFactory;

import java.util.Collections;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.glassfish.jersey.logging.LoggingFeature;

/*
    connect ServerVisitor with InstrumentedJettyServer
 */
public class SoapJettyServerFactory extends FactoryBase<JettyServer,Void,SoapJettyServerFactory> {
    public final FactoryReferenceAttribute<SoapHandler<HelloWorld>, SoapHandlerFactory<HelloWorld,Void,SoapJettyServerFactory>> soapHandler = new FactoryReferenceAttribute<>();
    public final FactoryReferenceListAttribute<HttpServerConnectorCreator,HttpServerConnectorFactory<Void,SoapJettyServerFactory>> connectors = new FactoryReferenceListAttribute<HttpServerConnectorCreator,HttpServerConnectorFactory<Void,SoapJettyServerFactory>>().setupUnsafe(HttpServerConnectorFactory.class).labelText("connectors").userNotSelectable();

    public SoapJettyServerFactory(){
        super();
        configLiveCycle().setCreator(() -> {

            ContextHandler context = new ContextHandler("/");
            context.setContextPath("/");
            context.setHandler(soapHandler.instance());

//            ServletContextHandler servletContextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
//            servletContextHandler.setContextPath("/");
//            servletContextHandler.addServlet(new ServletHolder(soapHandler.instance()),"/*");

            return new JettyServer(
                    connectors.instances(),
                    Collections.emptyList(),
                    Collections.singletonList(context),
                    ObjectMapperBuilder.buildNewObjectMapper(),
                    new LoggingFeature(new DelegatingLoggingFilterLogger()),
                    new DefaultResourceConfigSetup()

            );


        });
//        configLiveCycle().setReCreator(server->server.recreate(connectors.instances(),getResourcesInstances()));

        configLiveCycle().setStarter(JettyServer::start);
        configLiveCycle().setDestroyer(JettyServer::stop);

        config().setDisplayTextProvider(() -> "InstrumentedJettyServerFactory");

    }


}
