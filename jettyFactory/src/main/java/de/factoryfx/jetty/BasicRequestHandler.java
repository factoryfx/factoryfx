package de.factoryfx.jetty;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface BasicRequestHandler {

    /**
     * Handle a request with a simplified interface
     *
     * @param request
     *            The request either as the {@link Request} object or a wrapper of that request.
     * @param response
     *            The response as the {@link Response} object or a wrapper of that request.
     * @return true if the request has been handled, false otherwise
     */
    boolean handle(HttpServletRequest request, HttpServletResponse response) throws IOException;
}
