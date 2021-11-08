package io.github.factoryfx.jetty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Throwables;

import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;

public class AllExceptionMapper implements ExceptionMapper<Throwable> {
    private static final Logger logger = LoggerFactory.getLogger(AllExceptionMapper.class);

    @Override
    public Response toResponse(Throwable exception) {
        logger.error("", exception);
        if (exception.getClass()==NotFoundException.class) {
            return Response.status(Response.Status.NOT_FOUND).type(MediaType.TEXT_PLAIN_TYPE).entity("\n"+Throwables.getStackTraceAsString(Throwables.getRootCause(exception))).build();
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).type(MediaType.TEXT_PLAIN_TYPE).entity("\n"+Throwables.getStackTraceAsString(Throwables.getRootCause(exception))).build();
    }
}
