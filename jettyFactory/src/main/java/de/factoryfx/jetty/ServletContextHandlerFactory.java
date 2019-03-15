package de.factoryfx.jetty;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.ErrorHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class ServletContextHandlerFactory<R extends FactoryBase<?,R>> extends SimpleFactoryBase<Handler,R> {
    @SuppressWarnings("unchecked")
    public final FactoryReferenceAttribute<UpdateableServlet,UpdateableServletFactory<R>> updatableRootServlet = FactoryReferenceAttribute.create(new FactoryReferenceAttribute<>(UpdateableServletFactory.class).labelText("updatableRootServlet"));

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
