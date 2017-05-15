package de.factoryfx.servlet;

import javax.servlet.ServletContext;


public class ServletContextAwareVisitor {
    public final ServletContext servletContext;

    public ServletContextAwareVisitor(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}
