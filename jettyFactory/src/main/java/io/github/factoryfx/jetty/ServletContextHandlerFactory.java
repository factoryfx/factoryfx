package io.github.factoryfx.jetty;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.ErrorHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class ServletContextHandlerFactory<R extends FactoryBase<?,R>> extends SimpleFactoryBase<Handler,R> {

    public final FactoryAttribute<R,UpdateableServlet,UpdateableServletFactory<R>> updatableRootServlet = new FactoryAttribute<R,UpdateableServlet,UpdateableServletFactory<R>>().labelText("updatableRootServlet");

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
