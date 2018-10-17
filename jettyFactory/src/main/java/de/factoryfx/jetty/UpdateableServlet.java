package de.factoryfx.jetty;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * workaround for servlet limitation, you can't dynamically add/remove servlet if the context is started.
 */
public class UpdateableServlet implements Servlet {

    private volatile List<BasicRequestHandler> basicRequestHandler;
    private volatile Servlet servlet;

    public UpdateableServlet(Servlet servlet, Collection<BasicRequestHandler> basicRequestHandlers) {
        this.servlet = servlet;
        this.basicRequestHandler = basicRequestHandlers==null? Collections.emptyList():new ArrayList<>(basicRequestHandlers);
    }

    public void update(Servlet servlet, Collection<BasicRequestHandler> basicRequestHandlers){
        try {
            servlet.init(this.servlet.getServletConfig());
        } catch (ServletException e) {
            throw new RuntimeException(e);
        }
        this.servlet=servlet;
        this.basicRequestHandler = basicRequestHandlers==null? Collections.emptyList():new ArrayList<>(basicRequestHandlers);
    }


    @Override
    public void init(ServletConfig config) throws ServletException {
        servlet.init(config);
    }

    @Override
    public ServletConfig getServletConfig() {
        return servlet.getServletConfig();
    }

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        List<BasicRequestHandler> handlerCopy = this.basicRequestHandler;
        for (BasicRequestHandler handler : handlerCopy) {
            if (handler.handle((HttpServletRequest)req,(HttpServletResponse)res))
                return;
        }
        servlet.service(req,res);
    }

    @Override
    public String getServletInfo() {
        return servlet.getServletInfo();
    }

    @Override
    public void destroy() {
        servlet.destroy();
    }
}
