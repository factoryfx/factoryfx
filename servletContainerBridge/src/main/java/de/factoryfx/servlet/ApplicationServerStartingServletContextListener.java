package de.factoryfx.servlet;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.server.Microservice;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;


//@WebListener
public abstract class ApplicationServerStartingServletContextListener implements ServletContextListener {

    private Microservice<? super ServletContextAwareVisitor, ? extends FactoryBase<?, ? super ServletContextAwareVisitor,?>,?> microservice;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();
        microservice = createFactoryFxApplicationServer();
        microservice.start();
        microservice.query(new ServletContextAwareVisitor(context));
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }

    protected abstract Microservice<? super ServletContextAwareVisitor,? extends FactoryBase<?,? super ServletContextAwareVisitor,?>,?> createFactoryFxApplicationServer();
}
