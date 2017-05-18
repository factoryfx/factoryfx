package de.factoryfx.servlet;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.server.ApplicationServer;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;


//@WebListener
public abstract class ApplicationServerStartingServletContextListener implements ServletContextListener {

    private ApplicationServer<? super ServletContextAwareVisitor, ?, ? extends FactoryBase<?, ? super ServletContextAwareVisitor>> applicationServer;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();
        applicationServer = createFactoryFxApplicationServer();
        applicationServer.start();
        applicationServer.query(new ServletContextAwareVisitor(context));
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }

    protected abstract ApplicationServer<? super ServletContextAwareVisitor,?,? extends FactoryBase<?,? super ServletContextAwareVisitor>> createFactoryFxApplicationServer();
}
