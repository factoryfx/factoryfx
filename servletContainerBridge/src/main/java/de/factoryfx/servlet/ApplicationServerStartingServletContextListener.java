package de.factoryfx.servlet;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.server.ApplicationServer;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.function.Consumer;


//@WebListener
public abstract class ApplicationServerStartingServletContextListener<V extends ServletContextAwareVisitor,L ,T extends FactoryBase<L,V>> implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();

        ApplicationServer<L,V,T>  applicationServer = createFactoryFxApplicationServer();

        applicationServer.start();
        applicationServer.query((V)new ServletContextAwareVisitor(context));
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }

    protected abstract ApplicationServer<L,V,T> createFactoryFxApplicationServer();
}
