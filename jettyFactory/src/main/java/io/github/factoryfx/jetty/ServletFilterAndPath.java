package io.github.factoryfx.jetty;

import org.eclipse.jetty.http.pathmap.ServletPathSpec;

import javax.servlet.Filter;
import javax.servlet.Servlet;

public class ServletFilterAndPath {

    public final String pathSpec;
    public final Filter filter;

    public ServletFilterAndPath(String pathSpec, Filter filter) {
        this.pathSpec = pathSpec;
        this.filter = filter;
    }
}
