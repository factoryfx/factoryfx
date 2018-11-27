package de.factoryfx.microservice.rest.client;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import de.factoryfx.data.attribute.primitive.BooleanAttribute;
import de.factoryfx.data.attribute.primitive.IntegerAttribute;
import de.factoryfx.data.attribute.types.ObjectValueAttribute;
import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.FactoryTreeBuilderBasedAttributeSetup;
import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.microservice.common.MicroserviceResourceApi;
import de.factoryfx.server.user.persistent.PasswordHash;
import org.glassfish.jersey.CommonProperties;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.client.proxy.WebResourceFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;


public class MicroserviceRestClientBuilder {


    /**
     * For user management
     * @param host host
     * @param port port
     * @param user user
     * @param passwordNotHashed password in clear text
     * @param serverRootClass  server class, cause type erasure
     * @param <V> Visitor client
     * @param <R>  Root client
     * @param <VS> Visitor server
     * @param <RS> Root Server
     * @param <S> Summary Data for factory history
     * @return client
     */
    public static <V, R extends FactoryBase<?,V,R>,VS, RS extends FactoryBase<?,VS,RS>,S> MicroserviceRestClient<VS,RS,S> build(String host, int port, String user, String passwordNotHashed, Class<RS> serverRootClass){
        MicroserviceRestClientFactory<V,R,VS, RS,S> microserviceRestClientFactory = new MicroserviceRestClientFactory<>();
        microserviceRestClientFactory.port.set(port);
        microserviceRestClientFactory.host.set(host);
        microserviceRestClientFactory.user.set(user);
        microserviceRestClientFactory.passwordHash.set(new PasswordHash().hash(passwordNotHashed));
        microserviceRestClientFactory.factoryRootClass.set(serverRootClass);
        return microserviceRestClientFactory.createClient();
    }

    /**
     *  For no user management
     * @param host host
     * @param port port
     * @param serverRootClass server class, cause type erasure
     * @param <V> Visitor client
     * @param <R>  Root client
     * @param <VS> Visitor server
     * @param <RS> Root Server
     * @param <S> Summary Data for factory history
     * @return client
     */
    public static <V, R extends FactoryBase<?,V,R>,VS, RS extends FactoryBase<?,VS,RS>,S> MicroserviceRestClient<VS,RS,S> build(String host, int port, Class<RS> serverRootClass){
        MicroserviceRestClientFactory<V,R,VS, RS,S> microserviceRestClientFactory = new MicroserviceRestClientFactory<>();
        microserviceRestClientFactory.port.set(port);
        microserviceRestClientFactory.host.set(host);
        microserviceRestClientFactory.user.set(null);
        microserviceRestClientFactory.passwordHash.set(null);
        microserviceRestClientFactory.factoryRootClass.set(serverRootClass);
        return microserviceRestClientFactory.createClient();
    }
}
