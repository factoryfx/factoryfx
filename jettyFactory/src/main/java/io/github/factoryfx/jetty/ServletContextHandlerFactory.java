package io.github.factoryfx.jetty;

import java.util.EnumSet;

import org.eclipse.jetty.ee10.servlet.FilterHolder;
import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.ee10.servlet.ServletHolder;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.ErrorHandler;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;
import io.github.factoryfx.factory.attribute.dependency.FactoryListAttribute;
import jakarta.servlet.DispatcherType;

public class ServletContextHandlerFactory<R extends FactoryBase<?,R>> extends SimpleFactoryBase<Handler,R> {

    public final FactoryAttribute<UpdateableServlet,UpdateableServletFactory<R>> updatableRootServlet = new FactoryAttribute<UpdateableServlet,UpdateableServletFactory<R>>().labelText("updatableRootServlet");
    public final FactoryListAttribute<ServletFilterAndPath,ServletFilterAndPathFactory<R>> servletFilters = new FactoryListAttribute<ServletFilterAndPath,ServletFilterAndPathFactory<R>>().labelText("updatableRootServlet");


    @Override
    protected Handler createImpl() {
        ServletHolder holder = new ServletHolder(updatableRootServlet.instance());
        ServletContextHandler contextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        contextHandler.addServlet( holder, "/*");
        ErrorHandler errorHandler = new ErrorHandler();
        errorHandler.setShowStacks(true);
        contextHandler.setErrorHandler(errorHandler);
        for (ServletFilterAndPath servletFilterAndPath : servletFilters.instances()) {
            contextHandler.addFilter(new FilterHolder(servletFilterAndPath.filter), servletFilterAndPath.pathSpec, EnumSet.allOf(DispatcherType.class));
        }
        return contextHandler;
    }
}
