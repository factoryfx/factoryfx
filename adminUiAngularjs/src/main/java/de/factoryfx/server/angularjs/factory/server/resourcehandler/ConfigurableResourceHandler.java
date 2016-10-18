package de.factoryfx.server.angularjs.factory.server.resourcehandler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.http.MimeTypes;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

public class ConfigurableResourceHandler extends AbstractHandler {
    MimeTypes mimeTypes = new MimeTypes();

    private final FileContentProvider fileContentProvider;
    private final ETagProvider etagProvider;

    public ConfigurableResourceHandler(FileContentProvider fileContentProvider, ETagProvider etagProvider){
        this.fileContentProvider=fileContentProvider;
        this.etagProvider=etagProvider;
    }


    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if (!HttpMethod.GET.asString().equals(request.getMethod())) {
            if (!HttpMethod.HEAD.asString().equals(request.getMethod())) {
                return;
            }
        }
        String file=target;
        if (file.startsWith("/")){
            file=file.replaceFirst("/","");
        }
        if (fileContentProvider.containsFile(file) || "/".equals(target)){

            String ifnm = request.getHeader(HttpHeader.IF_NONE_MATCH.asString());
            String etag = etagProvider.getEtag();

            if (ifnm != null && ifnm.equals(etag)) {
                baseRequest.getResponse().setStatus(HttpStatus.NOT_MODIFIED_304);
                baseRequest.setHandled(true);
                return;
            }
            baseRequest.getResponse().addHeader("ETag",etag);

            if ("/".equals(target)){
                writeResponse(response, "index.html");
            } else {
                writeResponse(response, file);
            }
            baseRequest.setHandled(true);
        } else {
        }
    }

    private void writeResponse(HttpServletResponse response, String file) throws IOException {
        String mimeByExtension = mimeTypes.getMimeByExtension(file);
        if (mimeByExtension!=null){
            response.setContentType(mimeByExtension);
        }

        byte[] fileContent = fileContentProvider.getFile(file);
        response.setContentLength(fileContent.length);
        response.getOutputStream().write(fileContent);
    }

}
