package io.github.factoryfx.microservice.rest.client;

import com.fasterxml.jackson.core.StreamReadConstraints;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.glassfish.jersey.CommonProperties;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.client.proxy.WebResourceFactory;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JacksonJaxbJsonProvider;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.FactoryTreeBuilderBasedAttributeSetup;
import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.primitive.BooleanAttribute;
import io.github.factoryfx.factory.attribute.primitive.IntegerAttribute;
import io.github.factoryfx.factory.attribute.types.ObjectValueAttribute;
import io.github.factoryfx.factory.attribute.types.StringAttribute;
import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;
import io.github.factoryfx.microservice.common.MicroserviceResourceApi;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;

/**
 *
 * @param <R>  Root client
 * @param <RS> Root Server
 */
public class MicroserviceRestClientFactory<R extends FactoryBase<?,R>, RS extends FactoryBase<?,RS>> extends SimpleFactoryBase<MicroserviceRestClient<RS>,R> {
    public final StringAttribute user = new StringAttribute().en("user").nullable();
    public final StringAttribute passwordHash = new StringAttribute().en("passwordHash").nullable();


    public final BooleanAttribute ssl=new BooleanAttribute().labelText("ssl");
    public final StringAttribute host=new StringAttribute().labelText("host");
    public final IntegerAttribute port=new IntegerAttribute().labelText("port");
    public final StringAttribute path=new StringAttribute().labelText("path").nullable();

    public final StringAttribute httpAuthenticationUser=new StringAttribute().labelText("httpAuthenticationUser").nullable();
    public final StringAttribute httpAuthenticationPassword=new StringAttribute().labelText("httpAuthenticationPassword").nullable();

    public final ObjectValueAttribute<FactoryTreeBuilderBasedAttributeSetup<RS>> factoryTreeBuilderBasedAttributeSetup=new ObjectValueAttribute<FactoryTreeBuilderBasedAttributeSetup<RS>>().labelText("factoryTreeBuilderBasedAttributeSetup").nullable();

    public final IntegerAttribute maxStringLength = new IntegerAttribute().labelText("ObjectMapper max String length").defaultValue(20_000_000);

    public MicroserviceRestClientFactory(){
        config().setDisplayTextProvider(this::getUrl);
    }

    private String getUrl() {
        String path = this.path.get();
        if (path==null){
            path ="";
        }
        return (ssl.get() ? "https" : "http") + "://" + host.get() + ":" + port.get() + "/" + path;
    }

    @Override
    protected MicroserviceRestClient<RS> createImpl() {
        return createClient();
    }

    /**
     * shortcut to use the client in non factory context
     *
     * @return client
     **/
    @SuppressWarnings("unchecked")
    public MicroserviceRestClient<RS> createClient() {
        JacksonJaxbJsonProvider jacksonProvider = new JacksonJaxbJsonProvider();
        ObjectMapper objectMapper = ObjectMapperBuilder.buildNewObjectMapper();

        StreamReadConstraints streamReadConstraints = StreamReadConstraints
                .builder()
                .maxStringLength(maxStringLength.get())
                .build();

        objectMapper.getFactory().setStreamReadConstraints(streamReadConstraints);


        jacksonProvider.setMapper(objectMapper);
        ClientConfig configuration = new ClientConfig(new ClientConfig());
        configuration.property(CommonProperties.FEATURE_AUTO_DISCOVERY_DISABLE, true);
        configuration.register(jacksonProvider);

//        configuration.property(ClientProperties.CONNECT_TIMEOUT, 30000);
//        configuration.property(ClientProperties.READ_TIMEOUT, 30000);


        Client client = ClientBuilder.newClient(configuration);
        if (!httpAuthenticationUser.isEmpty() && !httpAuthenticationPassword.isEmpty()){
            HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic(httpAuthenticationUser.get(), httpAuthenticationPassword.get());
            client.register(feature);
        }



        WebTarget webTarget = client.target(getUrl());
        MicroserviceResourceApi<RS> microserviceResourceApi = (MicroserviceResourceApi<RS>) WebResourceFactory.newResource(MicroserviceResourceApi.class, webTarget);


        return new MicroserviceRestClient<>(microserviceResourceApi,user.get(),passwordHash.get(),factoryTreeBuilderBasedAttributeSetup.get());
    }


}
