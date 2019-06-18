package io.github.factoryfx.dom.rest;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.*;
import java.util.function.Function;

import io.github.factoryfx.factory.merge.MergeDiffInfo;
import io.github.factoryfx.factory.storage.DataUpdate;
import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.storage.StoredDataMetadata;
import io.github.factoryfx.factory.log.FactoryUpdateLog;
import io.github.factoryfx.microservice.rest.MicroserviceResource;
import io.github.factoryfx.server.Microservice;
import io.github.factoryfx.server.user.AuthorizedUser;
import io.github.factoryfx.server.user.UserManagement;
import io.github.factoryfx.microservice.common.*;
import org.eclipse.jetty.http.MimeTypes;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class MicroserviceDomResource<R extends FactoryBase<?,R>,S> extends MicroserviceResource<R,S> {
    private final StaticFileAccess staticFileAccess;

    public MicroserviceDomResource(Microservice<?, R, S> microservice, UserManagement userManagement, StaticFileAccess staticFileAccess) {
        super(microservice, userManagement);
        this.staticFileAccess = staticFileAccess;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/dynamicDataDictionary")
    public DynamicDataDictionary getDynamicDataDictionary() {
        return new DynamicDataDictionary(microservice.prepareNewFactory().root);
    }

    @GET
    @Path("/{path:.*}")
    public Response get(@PathParam("path") String path) {
        if (com.google.common.io.Files.getFileExtension(path).isEmpty()) {//https://github.com/Microsoft/TypeScript/issues/16577
            path=path+".js";
        }

        MimeTypes mimeTypes = new MimeTypes();
        return Response.ok(staticFileAccess.getFile(path)).
                header("Content-Type", mimeTypes.getMimeByExtension(path)).build();

    }
}
