package de.factoryfx.development.angularjs.server;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.server.ContainerRequest;

public class AuthorizationRequestFilter implements ContainerRequestFilter {
    @Context
    HttpServletRequest webRequest;

    @Override
    public void filter(ContainerRequestContext requestContext)
                    throws IOException {
        if (webRequest.getSession(false) == null) {
            if (
                !((ContainerRequest) requestContext).getPath(true).equals("login") &&
                !((ContainerRequest) requestContext).getPath(true).equals("locales") &&
                !((ContainerRequest) requestContext).getPath(true).equals("guimodel")
               ) {
                requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).type(MediaType.TEXT_PLAIN_TYPE).entity("User cannot access the resource.").build());
            }
        }
    }
//        final SecurityContext securityContext =
//                    requestContext.getSecurityContext();
//        if (securityContext == null || !securityContext.isUserInRole("privileged")) {
//
//            if (!((ContainerRequest)requestContext).getPath(true).equals("login")){
//                requestContext.abortWith(Response
//                        .status(Response.Status.UNAUTHORIZED)
//                        .type(MediaType.TEXT_PLAIN_TYPE)
//                        .entity("User cannot access the resource.")
//                        .build());
//            }
//
//        }
//    }
}