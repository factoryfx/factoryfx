package de.factoryfx.server.rest.client;

import de.factoryfx.data.attribute.types.ObjectValueAttribute;
import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;

/**
 *
 * @param <V> Visitor client
 * @param <R>  Root client
 * @param <VS> Visitor server
 * @param <RS> Root Server
 */
public class ApplicationServerRestClientFactory<V, R extends FactoryBase<?,V,R>,VS, RS extends FactoryBase<?,VS,RS>> extends SimpleFactoryBase<ApplicationServerRestClient<VS, RS>,V,R> {
    public final FactoryReferenceAttribute<RestClient,RestClientFactory<VS,RS>> restClient= new FactoryReferenceAttribute<RestClient,RestClientFactory<VS,RS>>().setupUnsafe(RestClientFactory.class);//.en("rest client");
    public final ObjectValueAttribute<Class<RS>> factoryRootClass = new ObjectValueAttribute<>();//.en("factoryRootClass");
    public final StringAttribute user = new StringAttribute().en("user").nullable();
    public final StringAttribute passwordHash = new StringAttribute().en("passwordHash").nullable();

    @Override
    public ApplicationServerRestClient<VS, RS> createImpl() {
        return new ApplicationServerRestClient<>(restClient.instance(),factoryRootClass.get(),user.get(),passwordHash.get());
    }

}
