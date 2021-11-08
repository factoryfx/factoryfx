package io.github.factoryfx.jetty;

import jakarta.servlet.Filter;

public class ServletFilterAndPath {

    public final String pathSpec;
    public final Filter filter;

    public ServletFilterAndPath(String pathSpec, Filter filter) {
        this.pathSpec = pathSpec;
        this.filter = filter;
    }
}
