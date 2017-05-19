package de.factoryfx.server.rest.server;

import javax.servlet.*;
import java.io.IOException;

/**
 * workaround for servlet limitation, you can't dynamically add/remove servlet if the context is started.
 */
public class UpdateableServlet implements Servlet {

    private Servlet servlet;

    public UpdateableServlet(Servlet servlet) {
        this.servlet = servlet;

    }

    public synchronized void update(Servlet servlet){
        try {
            servlet.init(this.servlet.getServletConfig());
        } catch (ServletException e) {
            throw new RuntimeException(e);
        }
        this.servlet=servlet;
    }


    @Override
    public synchronized void init(ServletConfig config) throws ServletException {
        servlet.init(config);
    }

    @Override
    public synchronized ServletConfig getServletConfig() {
        return servlet.getServletConfig();
    }

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        servlet.service(req,res);
    }

    @Override
    public synchronized String getServletInfo() {
        return servlet.getServletInfo();
    }

    @Override
    public synchronized void destroy() {
        servlet.destroy();
    }
}
