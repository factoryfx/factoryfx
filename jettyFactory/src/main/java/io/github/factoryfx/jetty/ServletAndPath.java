package io.github.factoryfx.jetty;

import org.eclipse.jetty.http.pathmap.MatchedPath;
import org.eclipse.jetty.http.pathmap.ServletPathSpec;

import jakarta.servlet.Servlet;

import java.util.Optional;
import java.util.function.Function;

public record ServletAndPath(ServletPathSpec pathSpec, Servlet servlet) {

    public ServletAndPath(String pathSpec, Servlet servlet) {
        this(new ServletPathSpec(pathSpec), servlet);
    }

    public String getPathMatch(String servletPath) {
        return getPathMatchOrNull(servletPath, MatchedPath::getPathMatch);
    }

    public String getPathInfo(String servletPath) {
        return getPathMatchOrNull(servletPath, MatchedPath::getPathInfo);
    }

    private String getPathMatchOrNull(String servletPath, Function<MatchedPath, String> pathMatchFunction) {
        return Optional.ofNullable(pathSpec.matched(servletPath)).map(pathMatchFunction).orElse(null);
    }
}
