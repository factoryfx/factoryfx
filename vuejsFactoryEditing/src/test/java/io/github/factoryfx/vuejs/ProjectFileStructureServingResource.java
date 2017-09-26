package io.github.factoryfx.vuejs;

import com.google.common.io.ByteStreams;
import org.eclipse.jetty.http.MimeTypes;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import java.io.*;
import java.nio.file.Paths;

@Path("/")
public class ProjectFileStructureServingResource {
    MimeTypes mimeTypes = new MimeTypes();

    @GET
    @Path("/{path: .*}")
    public Response get(@Context HttpServletRequest httpRequest){
        String path=httpRequest.getPathInfo();
        if ("/".equals(path)){
            path="/index.html";
        }

        String mimeByExtension = mimeTypes.getMimeByExtension(path);

        String filepath="src/test/resources/io/github/factoryfx/vuejs"+path;
        if (new File(filepath).exists()){
            try (InputStream inputStream=new FileInputStream(filepath)){
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                ByteStreams.copy(inputStream, outputStream);
                return Response.ok(outputStream.toByteArray(),mimeByExtension).build();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        filepath="src/main/resources/io/github/factoryfx/vuejs"+path;
        if (new File(filepath).exists()){
            try (InputStream inputStream=new FileInputStream(filepath)){
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                ByteStreams.copy(inputStream, outputStream);
                return Response.ok(outputStream.toByteArray(),mimeByExtension).build();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

}
