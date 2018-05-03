package de.factoryfx.util.rest.server;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import com.google.common.base.Throwables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AllExceptionMapper implements ExceptionMapper<Throwable> {
    private static final Logger logger = LoggerFactory.getLogger(AllExceptionMapper.class);

    @Override
    public Response toResponse(Throwable exception) {
        logger.error("", exception);
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).type(MediaType.TEXT_PLAIN_TYPE).entity("\n"+Throwables.getStackTraceAsString(Throwables.getRootCause(exception))).build();
    }
}
