package de.factoryfx.jetty;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

public class KeyedServlet {
    private final Servlet servlet;

    KeyedServlet(Servlet servlet) {
        this.servlet = servlet;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof KeyedServlet))
            return false;
        return this.servlet == ((KeyedServlet)obj).servlet;
    }

    @Override
    public int hashCode() {
        return System.identityHashCode(servlet);
    }

    public void destroy(){
        servlet.destroy();
    }

    public void init(ServletConfig config) throws ServletException {
        servlet.init(config);
    }

    public Servlet getServlet()  {
        return servlet;
    }

}
