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
public class ApplicationServerRestClientFactory<V, R extends FactoryBase<?,V>,VS, RS extends FactoryBase<?,VS>> extends SimpleFactoryBase<ApplicationServerRestClient<VS, RS>,V> {
    public final FactoryReferenceAttribute<RestClient,RestClientFactory<VS>> restClient= new FactoryReferenceAttribute<RestClient,RestClientFactory<VS>>().setupUnsafe(RestClientFactory.class);//.en("rest client");
    public final ObjectValueAttribute<Class<RS>> factoryRootClass = new ObjectValueAttribute<>();//.en("factoryRootClass");
    public final StringAttribute user = new StringAttribute().en("user");
    public final StringAttribute passwordHash = new StringAttribute().en("passwordHash");

    @Override
    public ApplicationServerRestClient<VS, RS> createImpl() {
        return new ApplicationServerRestClient<>(restClient.instance(),factoryRootClass.get(),user.get(),passwordHash.get());
    }

}
