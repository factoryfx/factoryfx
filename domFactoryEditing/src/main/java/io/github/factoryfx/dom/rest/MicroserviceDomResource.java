package io.github.factoryfx.dom.rest;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.github.factoryfx.factory.FactoryTreeBuilderBasedAttributeSetup;
import io.github.factoryfx.factory.attribute.Attribute;
import io.github.factoryfx.factory.attribute.dependency.*;
import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.types.EncryptedString;
import io.github.factoryfx.factory.attribute.types.EncryptedStringAttribute;
import io.github.factoryfx.microservice.rest.MicroserviceResource;
import io.github.factoryfx.server.Microservice;
import io.github.factoryfx.server.user.UserManagement;
import org.eclipse.jetty.http.MimeTypes;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class MicroserviceDomResource<R extends FactoryBase<?,R>> extends MicroserviceResource<R> {
    private final StaticFileAccess staticFileAccess;
    private final FactoryTreeBuilderBasedAttributeSetup<R> factoryTreeBuilderBasedAttributeSetup;
    private final Function<R,List<GuiNavbarItem>> guiNavbarItemCreator;
    private final String projectName;

    public MicroserviceDomResource(Microservice<?, R> microservice, UserManagement userManagement, StaticFileAccess staticFileAccess, FactoryTreeBuilderBasedAttributeSetup<R> factoryTreeBuilderBasedAttributeSetup, Function<R, List<GuiNavbarItem>> guiNavbarItemCreator, String projectName) {
        super(microservice, userManagement);
        this.staticFileAccess = staticFileAccess;
        this.factoryTreeBuilderBasedAttributeSetup = factoryTreeBuilderBasedAttributeSetup;
        this.guiNavbarItemCreator = guiNavbarItemCreator;
        this.projectName = projectName;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/metadata")
    public DomGuiMetadata getDynamicDataDictionary() {
        R root = microservice.prepareNewFactory().root;
        return new DomGuiMetadata(new DynamicDataDictionary(root),new GuiConfiguration(projectName,guiNavbarItemCreator.apply(root)));
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

    @POST
    @Path("/createNewFactory")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @SuppressWarnings("unchecked")
    public FactoryBase<?,R> createNewFactory(AttributeAdressingRequest request) {
        Class newFactoryClass=null;
        Attribute<?, ?> attribute = resolveAttribute(request);
        if (attribute instanceof FactoryBaseAttribute){
            newFactoryClass=((FactoryBaseAttribute)attribute).internal_getReferenceClass();
        }
        if (attribute instanceof FactoryListBaseAttribute){
            newFactoryClass=((FactoryListBaseAttribute)attribute).internal_getReferenceClass();
        }
        factoryTreeBuilderBasedAttributeSetup.applyToRootFactoryDeep(microservice.prepareNewFactory().root);
        return (FactoryBase<?, R>) factoryTreeBuilderBasedAttributeSetup.createNewFactory(newFactoryClass).get(0);
    }
    
    public static class AttributeAdressingRequest {
        public String factoryId;
        public String attributeVariableName;
        public FactoryBase<?,?> root;
    }

    public static class ResolveViewResponse{
        public String factoryId;

        public ResolveViewResponse(String factoryId) {
            this.factoryId = factoryId;
        }
    }


    @POST
    @Path("/resolveViewRequest")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @SuppressWarnings("unchecked")
    public ResolveViewResponse resolveViewRequest(AttributeAdressingRequest request) {
        return new ResolveViewResponse(((FactoryViewAttribute<R,?,?>)resolveAttribute(request)).get().getId().toString());
    }

    private Attribute<?, ?> resolveAttribute(AttributeAdressingRequest request) {
        request.root.internal().finalise();
        Map<UUID, ? extends FactoryBase<?, ?>> uuidFactoryBaseMap = request.root.internal().collectChildFactoryMap();
        return uuidFactoryBaseMap.get(UUID.fromString(request.factoryId)).internal().getAttribute(request.attributeVariableName);
    }

    @POST
    @Path("/resolveViewList")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @SuppressWarnings("unchecked")
    public List<String> resolveViewList(AttributeAdressingRequest request) {
        return ((FactoryViewListAttribute<R,?,?>)resolveAttribute(request)).get().stream().map(f->f.getId().toString()).collect(Collectors.toList());
    }

    @POST
    @Path("/decryptAttribute")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public DecryptAttributeResponse decryptAttribute(DecryptAttributeRequest request) {
        return new DecryptAttributeResponse(((EncryptedStringAttribute)resolveAttribute(request)).get().decrypt(request.key));
    }


    public static class DecryptAttributeRequest extends AttributeAdressingRequest {
        public String key;
    }

    public static class DecryptAttributeResponse{
        public String text;

        public DecryptAttributeResponse(String text) {
            this.text = text;
        }
    }

    @POST
    @Path("/encryptAttribute")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public EncryptAttributeResponse encryptAttribute(EncryptAttributeRequest request) {
        ((EncryptedStringAttribute)resolveAttribute(request)).set(new EncryptedString(request.text,request.key));
        return new EncryptAttributeResponse();

    }


    public static class EncryptAttributeRequest extends AttributeAdressingRequest {
        public String text;
        public String key;
    }

    public static class EncryptAttributeResponse{

    }



}
