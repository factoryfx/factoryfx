package de.factoryfx.microservice.rest.client;

import de.factoryfx.data.attribute.types.ObjectValueAttribute;
import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.microservice.rest.client.MicroserviceRestClient;
import de.factoryfx.util.rest.client.RestClient;
import de.factoryfx.util.rest.client.RestClientFactory;

/**
 *
 * @param <V> Visitor client
 * @param <R>  Root client
 * @param <VS> Visitor server
 * @param <RS> Root Server
 */
public class MicroserviceRestClientFactory<V, R extends FactoryBase<?,V,R>,VS, RS extends FactoryBase<?,VS,RS>> extends SimpleFactoryBase<MicroserviceRestClient<VS, RS>,V,R> {
    public final FactoryReferenceAttribute<RestClient,RestClientFactory<VS,RS>> restClient= new FactoryReferenceAttribute<RestClient,RestClientFactory<VS,RS>>().setupUnsafe(RestClientFactory.class);//.en("rest client");
    public final ObjectValueAttribute<Class<RS>> factoryRootClass = new ObjectValueAttribute<>();//.en("factoryRootClass");
    public final StringAttribute user = new StringAttribute().en("user").nullable();
    public final StringAttribute passwordHash = new StringAttribute().en("passwordHash").nullable();

    @Override
    public MicroserviceRestClient<VS, RS> createImpl() {
        return new MicroserviceRestClient<>(restClient.instance(),factoryRootClass.get(),user.get(),passwordHash.get());
    }

}
