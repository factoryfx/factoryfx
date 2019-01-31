package de.factoryfx.jetty;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.ErrorHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class ServletContextHandlerFactory<V,R extends FactoryBase<?,V,R>> extends SimpleFactoryBase<Handler,V,R> {
    @SuppressWarnings("unchecked")
    public final FactoryReferenceAttribute<UpdateableServlet,UpdateableServletFactory<V,R>> updatableRootServlet = FactoryReferenceAttribute.create(new FactoryReferenceAttribute<>(UpdateableServletFactory.class).labelText("updatableRootServlet"));

    @Override
    public Handler createImpl() {
        ServletHolder holder = new ServletHolder(updatableRootServlet.instance());
        ServletContextHandler contextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        contextHandler.addServlet( holder, "/*");
        ErrorHandler errorHandler = new ErrorHandler();
        errorHandler.setShowStacks(true);
        contextHandler.setErrorHandler(errorHandler);
        return contextHandler;
    }
}
