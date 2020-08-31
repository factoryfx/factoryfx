package io.github.factoryfx.dom.rest;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.github.factoryfx.factory.FactoryTreeBuilderBasedAttributeSetup;
import io.github.factoryfx.factory.attribute.AttributeAndMetadata;
import io.github.factoryfx.factory.attribute.dependency.*;
import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.types.EncryptedString;
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
    @Path("/createNewFactories")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public CreateNewFactoriesResponse createNewFactories(AttributeAdressingRequest request) {
        CreateNewFactoriesResponse<R> response = new CreateNewFactoriesResponse<>();

        AttributeAndMetadata attribute = resolveAttribute(request);
        if (attribute.attributeMetadata.liveObjectClass!=null) {
            response.factories = factoryTreeBuilderBasedAttributeSetup.createNewFactory(attribute.attributeMetadata.liveObjectClass);
            response.dynamicDataDictionary = new DynamicDataDictionary(response.factories);
            return response;
        }
        //TODO select dialog if multiple in gui
        throw new IllegalArgumentException("no liveObjectClass found for: "+request);
    }

    public static class CreateNewFactoriesResponse<R extends FactoryBase<?,R>>{
        public List<? extends FactoryBase<?, R>> factories;
        public DynamicDataDictionary dynamicDataDictionary;
    }
    
    public static class AttributeAdressingRequest {
        public String factoryId;
        public String attributeVariableName;
        public FactoryBase<?,?> root;

        @Override
        public String toString() {
            return "AttributeAdressingRequest{" + "factoryId='" + factoryId + '\'' + ", attributeVariableName='" + attributeVariableName + '\'' + ", root=" + root + '}';
        }
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
        return new ResolveViewResponse(((FactoryViewAttribute<R,?,?>)resolveAttribute(request).attribute).get().getId().toString());
    }

    private AttributeAndMetadata resolveAttribute(AttributeAdressingRequest request) {
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
        return ((FactoryViewListAttribute<R,?,?>)resolveAttribute(request).attribute).get().stream().map(f->f.getId().toString()).collect(Collectors.toList());
    }

    @POST
    @Path("/decryptAttribute")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public DecryptAttributeResponse decryptAttribute(DecryptAttributeRequest request) {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return new DecryptAttributeResponse(new EncryptedString(request.encryptedText).decrypt(request.key));
    }


    public static class DecryptAttributeRequest  {
        public String key;
        public String encryptedText;
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
        return new EncryptAttributeResponse(new EncryptedString(request.text,request.key).getEncryptedString());

    }


    public static class EncryptAttributeRequest{
        public String text;
        public String key;
    }

    public static class EncryptAttributeResponse{
        public String  encryptedText;

        public EncryptAttributeResponse(String encryptedText) {
            this.encryptedText = encryptedText;
        }
    }



}
