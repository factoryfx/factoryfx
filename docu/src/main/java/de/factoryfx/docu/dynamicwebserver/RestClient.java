package de.factoryfx.docu.dynamicwebserver;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.google.common.base.Strings;
import de.factoryfx.data.jackson.ObjectMapperBuilder;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.client.filter.EncodingFilter;
import org.glassfish.jersey.message.DeflateEncoder;
import org.glassfish.jersey.message.GZipEncoder;

import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;

public class RestClient {

    private final Client client;
    private final URI baseURI;

    public RestClient(String host, int port, String path, boolean ssl, String httpAuthenticationUser, String httpAuthenticationPassword) {
        this(buildURI(host, port, ssl, path),httpAuthenticationUser,httpAuthenticationPassword);
    }

    public RestClient(String host, int port, String path) {
        this(buildURI(host, port, false, path),null,null);
    }

    public RestClient(URI baseURI, String httpAuthenticationUser, String httpAuthenticationPassword, Client client) {
        this.baseURI = baseURI;
        this.client = client; createClient(httpAuthenticationUser,httpAuthenticationPassword);
    }

    public RestClient(URI baseURI, String httpAuthenticationUser, String httpAuthenticationPassword) {
        this(baseURI,httpAuthenticationUser,httpAuthenticationPassword,createClient(httpAuthenticationUser,httpAuthenticationPassword));
    }

    public <R> R post(String subPath, Object entity, Class<R> returnType) {
        final CreateRequestResult request = createRequest(subPath);
        Response response = request.builder.post(Entity.json(entity));
        checkResponseStatus(response,request.uri);
        return response.readEntity(returnType);
    }

    public <R> R get(String subPath, Class<R> returnType) {
        final CreateRequestResult request = createRequest(subPath);
        Response response = request.builder.get();
        checkResponseStatus(response,request.uri);
        return response.readEntity(returnType);
    }

    private void checkResponseStatus(Response response, URI uri) {
        if (response.getStatus() != 200) {
            throw new RuntimeException(""+response.getStatus()+"\nReceived http status code " + response.getStatus() + "\n" +uri+"\n" + response.readEntity(String.class));
        }
    }

    private static class CreateRequestResult{
        private final Invocation.Builder builder;
        private final URI uri;

        private CreateRequestResult(Invocation.Builder builder, URI uri) {
            this.builder = builder;
            this.uri = uri;
        }
    }


    private CreateRequestResult createRequest(String subPath) {
        final WebTarget target = client.target(baseURI.resolve(subPath));
        return new CreateRequestResult(target.request().accept(MediaType.APPLICATION_JSON_TYPE),target.getUri());
    }

    public Object get(String subPath) {
        return createRequest(subPath).builder.get().getEntity();
    }


    private static URI buildURI(String host, int port, boolean ssl, String path)  {
        try {
            return new URI((ssl?"https":"http")+"://"+host+":"+port+"/"+path+"/");
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("bad host name",e);
        }
    }


    private static Client createClient(String httpAuthenticationUser, String httpAuthenticationPassword) {
        ClientConfig cc = new ClientConfig();
        Client client = ClientBuilder.newBuilder().withConfig(cc).build();
        client.register(GZipEncoder.class);
        client.register(EncodingFilter.class);
        client.register(DeflateEncoder.class);
        if (!Strings.isNullOrEmpty(httpAuthenticationUser) && !Strings.isNullOrEmpty(httpAuthenticationPassword) ){
            client.register(HttpAuthenticationFeature.basic(httpAuthenticationUser, httpAuthenticationPassword));
        }
        JacksonJaxbJsonProvider provider = new JacksonJaxbJsonProvider();
        provider.setMapper(ObjectMapperBuilder.buildNewObjectMapper());
        client.register(provider);
        return client;
    }


}
