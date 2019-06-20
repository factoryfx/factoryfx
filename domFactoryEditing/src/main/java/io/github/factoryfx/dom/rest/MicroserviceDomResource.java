package io.github.factoryfx.dom.rest;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.*;
import java.util.function.Function;

import io.github.factoryfx.factory.AttributeVisitor;
import io.github.factoryfx.factory.FactoryTreeBuilderBasedAttributeSetup;
import io.github.factoryfx.factory.attribute.Attribute;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;
import io.github.factoryfx.factory.attribute.dependency.FactoryListAttribute;
import io.github.factoryfx.factory.merge.MergeDiffInfo;
import io.github.factoryfx.factory.metadata.FactoryMetadataManager;
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
    private final FactoryTreeBuilderBasedAttributeSetup<R,S> factoryTreeBuilderBasedAttributeSetup;

    public MicroserviceDomResource(Microservice<?, R, S> microservice, UserManagement userManagement, StaticFileAccess staticFileAccess, FactoryTreeBuilderBasedAttributeSetup<R, S> factoryTreeBuilderBasedAttributeSetup) {
        super(microservice, userManagement);
        this.staticFileAccess = staticFileAccess;
        this.factoryTreeBuilderBasedAttributeSetup = factoryTreeBuilderBasedAttributeSetup;
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

    public static class CreateNewFactoryRequest{
        public String javaClass;
        public String attributeVariableName;
    }

    @POST
    @Path("/createNewFactory")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @SuppressWarnings("unchecked")
    public FactoryBase<?,R> createNewFactory(CreateNewFactoryRequest request) {

        Class<?> factoryClass = getFactoryClass(request.javaClass);
        FactoryBase<?,?> factoryBase = FactoryMetadataManager.getMetadataUnsafe(factoryClass).newInstance();
        factoryBase.internal().finalise();

        Class[] newFactoryClass = new Class[1];
        factoryBase.internal().visitAttributesFlat((attributeVariableName, attribute) -> {
            if (attributeVariableName.equals(request.attributeVariableName)){
                if (attribute instanceof FactoryAttribute){
                    newFactoryClass[0]=((FactoryAttribute)attribute).internal_getReferenceClass();
                }
                if (attribute instanceof FactoryListAttribute){
                    newFactoryClass[0]=((FactoryListAttribute)attribute).internal_getReferenceClass();
                }
            }
        });
        factoryTreeBuilderBasedAttributeSetup.applyToRootFactoryDeep(microservice.prepareNewFactory().root);
        return (FactoryBase<?, R>) factoryTreeBuilderBasedAttributeSetup.createNewFactory(newFactoryClass[0]).get(0);
    }

    public Class<?> getFactoryClass(String javaClass){
        //check if passed class is a factory class for security
        for (String factoryClazz : getDynamicDataDictionary().classNameToItem.keySet()) {
            if (factoryClazz.equals(javaClass)){
                try {
                    return Class.forName(javaClass);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        throw new IllegalArgumentException("invalid class: "+javaClass);
    }
}
