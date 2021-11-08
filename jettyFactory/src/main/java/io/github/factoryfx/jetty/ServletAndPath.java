package io.github.factoryfx.jetty;

import org.eclipse.jetty.http.pathmap.ServletPathSpec;

import jakarta.servlet.Servlet;

public class ServletAndPath {

    public final ServletPathSpec pathSpec;
    public final Servlet servlet;

    public ServletAndPath(String pathSpec, Servlet servlet) {
        this.pathSpec = new ServletPathSpec(pathSpec);
        this.servlet = servlet;
    }


    public String getPathMatch(String servletPath) {
        return pathSpec.getPathMatch(servletPath);
    }


    public String getPathInfo(String servletPath) {
        return pathSpec.getPathInfo(servletPath);
    }
}
