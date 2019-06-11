package io.github.factoryfx.factory.typescript.generator.testserver;

import com.google.common.io.ByteStreams;
import org.eclipse.jetty.http.MimeTypes;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

@Path("/")
public class WebResource {

    final String response;

    public WebResource(String response) {
        this.response = response;
    }

    @GET
    @Path("/{path:.*}")
    public Response get(@PathParam("path") String path) {
        if (com.google.common.io.Files.getFileExtension(path).isEmpty()) {//https://github.com/Microsoft/TypeScript/issues/16577
            path=path+".js";

        }

        MimeTypes mimeTypes = new MimeTypes();
        try {
            return Response.ok(Files.readString(java.nio.file.Path.of("src/test/js/"+path))).
                    header("Content-Type", mimeTypes.getMimeByExtension(path)).build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
