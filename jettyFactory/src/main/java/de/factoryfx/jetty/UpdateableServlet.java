package de.factoryfx.jetty;

import org.eclipse.jetty.http.pathmap.ServletPathSpec;
import org.eclipse.jetty.server.Request;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * workaround for servlet limitation, you can't dynamically add/remove servlet if the context is started.
 */
public class UpdateableServlet implements Servlet {

    private volatile Map<KeyedServlet,List<ServletPathSpec>> servlets = Collections.emptyMap();
    private ServletConfig servletConfig = null;

    public UpdateableServlet() {
    }

    public void update(Map<KeyedServlet,List<ServletPathSpec>> newServlets){
        try {
            List<Servlet> removedServlets = servlets.keySet().stream().filter(s->!newServlets.containsKey(s)).map(ks->ks.servlet).collect(Collectors.toList());
            List<Servlet> addedServlets = newServlets.keySet().stream().filter(s->!servlets.containsKey(s)).map(ks->ks.servlet).collect(Collectors.toList());
            this.servlets = newServlets;
            removedServlets.forEach(s->s.destroy());
            if (servletConfig != null) {
                for (Servlet s : addedServlets) {
                    s.init(servletConfig);
                }
            }
        } catch (ServletException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void init(ServletConfig config) throws ServletException {
        this.servletConfig= config;
        for (KeyedServlet s : servlets.keySet()) {
            s.servlet.init(servletConfig);
        }
    }

    @Override
    public ServletConfig getServletConfig() {
        return servletConfig;
    }

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        HttpServletRequest httpReq = (HttpServletRequest)req;
        String pathMatch = null;
        String pathInfo = null;
        Servlet bestMatch = null;
        String servletPath = httpReq.getServletPath();
        for (Map.Entry<KeyedServlet, List<ServletPathSpec>> entry : servlets.entrySet()) {
            Servlet servlet = entry.getKey().servlet;
            List<ServletPathSpec> pathes = entry.getValue();
            for (ServletPathSpec spc : pathes) {
                String thisPathMatch = spc.getPathMatch(servletPath);
                if (thisPathMatch != null) {
                    if (pathMatch == null || thisPathMatch.length() > pathMatch.length()) {
                        pathMatch = thisPathMatch;
                        pathInfo = spc.getPathInfo(servletPath);
                        bestMatch = servlet;
                    }
                }
            }
        }
        if (bestMatch != null) {
            Request baseRequest = null;
            if (httpReq instanceof Request) {
                baseRequest = (Request)httpReq;
            }
            dispatch(bestMatch,servletPath,pathInfo,baseRequest,httpReq, (HttpServletResponse) res);
        }
    }

    @Override
    public String getServletInfo() {
        return "factoryfx updateable servlets";
    }

    @Override
    public void destroy() {
        for (KeyedServlet ks : servlets.keySet()) {
            ks.servlet.destroy();
        }
    }

    static final class KeyedServlet {
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
    }


    public void dispatch(Servlet servlet, String servletPath, String pathInfo, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
        final String oldPath = request.getServletPath();
        final String oldInfo = request.getPathInfo();

        try
        {
            //We will presumably always run under a jetty environment, yet we support other environments as well
            if (baseRequest != null) {
                baseRequest.setServletPath(servletPath);
                baseRequest.setPathInfo(pathInfo);
            } else {
                request = new HttpServletRequestWrapper(request) {
                    @Override
                    public String getPathInfo() {
                        return pathInfo;
                    }

                    @Override
                    public String getServletPath() {
                        return servletPath;
                    }
                };
            }
            servlet.service(request,response);
        }
        finally
        {
            if (baseRequest != null) {
                baseRequest.setServletPath(oldPath);
                baseRequest.setPathInfo(oldInfo);
            }
        }
    }

}
