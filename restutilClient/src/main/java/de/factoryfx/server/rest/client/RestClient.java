package de.factoryfx.server.rest.client;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.google.common.base.Strings;
import de.factoryfx.data.jackson.ObjectMapperBuilder;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.client.filter.EncodingFilter;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.message.DeflateEncoder;
import org.glassfish.jersey.message.GZipEncoder;

public class RestClient {

    private final Client client;
    private final URI baseURI;

    public RestClient(String host, int port, String path, boolean ssl, String httpAuthenticationUser, String httpAuthenticationPassword) {
        this(buildURI(host, port, ssl, path),httpAuthenticationUser,httpAuthenticationPassword);
    }

    public RestClient(URI baseURI, String httpAuthenticationUser, String httpAuthenticationPassword, Client client) {
        this.baseURI = baseURI;
        this.client = client; createClient(httpAuthenticationUser,httpAuthenticationPassword);
    }

    public RestClient(URI baseURI, String httpAuthenticationUser, String httpAuthenticationPassword) {
        this(baseURI,httpAuthenticationUser,httpAuthenticationPassword,createClient(httpAuthenticationUser,httpAuthenticationPassword));
    }

    public <R> R post(String subPath, Object entity, Class<R> returnType) {
        Response response = createRequest(subPath).post(Entity.json(entity));
        checkResponseStatus(response);
        return response.readEntity(returnType);
    }

    public <R> R get(String subPath, Class<R> returnType) {
        Response response = createRequest(subPath).get();
        checkResponseStatus(response);
        return response.readEntity(returnType);
    }

    private void checkResponseStatus(Response response) {
        if (response.getStatus() != 200)
            throw new RuntimeException("Received http status code "+response.getStatus()+"\n"+response.readEntity(String.class));
    }

    private Invocation.Builder createRequest(String subPath) {
        return client.target(baseURI.resolve(subPath)).request().accept(MediaType.APPLICATION_JSON_TYPE);
    }

    public Object get(String subPath) {
        return createRequest(subPath).get().getEntity();
    }


    private static URI buildURI(String host, int port, boolean ssl, String path)  {
        try {
            return new URI((ssl?"https":"http")+"://"+host+":"+port+"/"+path+"/");
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("bad host name",e);
        }
    }


    private static Client createClient(String httpAuthenticationUser, String httpAuthenticationPassword) {
        ClientConfig cc = new ClientConfig().register(new JacksonFeature());
        Client client = ClientBuilder.newBuilder().withConfig(cc).build();
        client.register(GZipEncoder.class);
        client.register(EncodingFilter.class);
        client.register(DeflateEncoder.class);
        if (!Strings.isNullOrEmpty(httpAuthenticationUser) && !Strings.isNullOrEmpty(httpAuthenticationPassword) ){
            client.register(HttpAuthenticationFeature.basic(httpAuthenticationUser, httpAuthenticationPassword));
        }
        JacksonJaxbJsonProvider provider = new JacksonJaxbJsonProvider();
        provider.setMapper(ObjectMapperBuilder.buildNew().getObjectMapper());
        client.register(provider);
        return client;
    }


}
