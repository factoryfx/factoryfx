package io.github.factoryfx.jetty;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.servlet.Servlet;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;

/**
 * workaround for servlet limitation, you can't dynamically add/remove servlet if the context is started.
 */
public class UpdateableServlet implements Servlet {

    private volatile Collection<ServletAndPath> servlets;
    private ServletConfig servletConfig = null;

    public UpdateableServlet(Collection<ServletAndPath> newServlets) {
        this.servlets = newServlets;
    }

    public void update(Collection<ServletAndPath> newServlets) {
        update(this.servlets, newServlets);
        this.servlets = newServlets;
    }

    private void update(Collection<ServletAndPath> previousServlets, Collection<ServletAndPath> newServlets) {
        Set<Servlet> previousServletsMapped = previousServlets.stream().map((s) -> s.servlet).collect(Collectors.toSet());

        newServlets.forEach(servletAndPath -> {
            try {
                if (!previousServletsMapped.contains(servletAndPath.servlet)) {
                    servletAndPath.servlet.init(servletConfig);
                }
            } catch (ServletException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void init(ServletConfig config) {
        this.servletConfig = config;
        update(List.of(), this.servlets);
    }

    @Override
    public ServletConfig getServletConfig() {
        return servletConfig;
    }

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        HttpServletRequest httpReq = (HttpServletRequest) req;
        String pathMatch = null;
        String pathInfo = null;
        Servlet bestMatch = null;
        String servletPath = httpReq.getRequestURI().substring(httpReq.getServletPath().length());
        for (ServletAndPath servletAndPath : servlets) {
            Servlet servlet = servletAndPath.servlet;
            String thisPathMatch = servletAndPath.getPathMatch(servletPath);
            if (thisPathMatch != null) {
                if (pathMatch == null || thisPathMatch.length() > pathMatch.length()) {
                    pathMatch = thisPathMatch;
                    pathInfo = servletAndPath.getPathInfo(servletPath);
                    bestMatch = servlet;
                }
            }
        }
        if (bestMatch != null) {
            dispatch(bestMatch, pathMatch, pathInfo, httpReq, res);
        } else {
            HttpServletResponse response = (HttpServletResponse) res;
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    public String getServletInfo() {
        return "factoryfx updateable servlets";
    }

    @Override
    public void destroy() {
    }

    public void dispatch(Servlet bestMatch, String servletPath, String pathInfo, HttpServletRequest request, ServletResponse response) throws IOException, ServletException {
        bestMatch.service(new HttpServletRequestWrapper(request) {
            @Override
            public String getPathInfo() {
                return pathInfo;
            }

            @Override
            public String getServletPath() {
                return servletPath;
            }
        }, response);
    }
}
