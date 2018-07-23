package de.factoryfx.microservice.rest.client;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import de.factoryfx.data.attribute.primitive.BooleanAttribute;
import de.factoryfx.data.attribute.primitive.IntegerAttribute;
import de.factoryfx.data.attribute.types.ObjectValueAttribute;
import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.microservice.common.MicroserviceResourceApi;
import org.glassfish.jersey.CommonProperties;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.proxy.WebResourceFactory;


import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;

/**
 *
 * @param <V> Visitor client
 * @param <R>  Root client
 * @param <VS> Visitor server
 * @param <RS> Root Server
 * @param <S> Summary Data for factory history
 */
public class MicroserviceRestClientFactory<V, R extends FactoryBase<?,V,R>,VS, RS extends FactoryBase<?,VS,RS>,S> extends SimpleFactoryBase<MicroserviceRestClient<VS, RS,S>,V,R> {
    //public final FactoryReferenceAttribute<RestClient,RestClientFactory<VS,RS>> restClient= new FactoryReferenceAttribute<RestClient,RestClientFactory<VS,RS>>().setupUnsafe(RestClientFactory.class);//.en("rest client");
    public final ObjectValueAttribute<Class<RS>> factoryRootClass = new ObjectValueAttribute<>();//.en("factoryRootClass");
    public final StringAttribute user = new StringAttribute().en("user").nullable();
    public final StringAttribute passwordHash = new StringAttribute().en("passwordHash").nullable();


    public final BooleanAttribute ssl=new BooleanAttribute().labelText("ssl");
    public final StringAttribute host=new StringAttribute().labelText("host");
    public final IntegerAttribute port=new IntegerAttribute().labelText("port");
    public final StringAttribute path=new StringAttribute().labelText("path").nullable();

    public final StringAttribute httpAuthenticationUser=new StringAttribute().labelText("httpAuthenticationUser").nullable();
    public final StringAttribute httpAuthenticationPassword=new StringAttribute().labelText("httpAuthenticationPassword").nullable();
//
//    @Override
//    public RestClient createImpl() {
//        return new RestClient(host.get(),port.get(),path.get(),ssl.get(),httpAuthenticationUser.get(),httpAuthenticationPassword.get());
//    }
//
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

    @SuppressWarnings("unchecked")
    @Override
    public MicroserviceRestClient<VS, RS, S> createImpl() {
        return createClient();
    }

    /**
     * shortcut to use the client in non factory context
     *
     * @return client
     **/
    public MicroserviceRestClient<VS, RS, S> createClient() {
        JacksonJaxbJsonProvider jacksonProvider = new JacksonJaxbJsonProvider();
        jacksonProvider.setMapper(ObjectMapperBuilder.buildNewObjectMapper());
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
        MicroserviceResourceApi<VS,RS,S> microserviceResourceApi = (MicroserviceResourceApi<VS,RS,S>) WebResourceFactory.newResource(MicroserviceResourceApi.class, webTarget);


        return new MicroserviceRestClient<>(microserviceResourceApi,factoryRootClass.get(),user.get(),passwordHash.get());
    }


}
