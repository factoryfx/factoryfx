package de.factoryfx.server.rest.client;

import de.factoryfx.data.attribute.types.ObjectValueAttribute;
import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;

public class ApplicationServerRestClientFactory<V,T extends FactoryBase<?,V>> extends SimpleFactoryBase<ApplicationServerRestClient<V,T>,V> {
    public final FactoryReferenceAttribute<RestClient,RestClientFactory<V>> restClient= new FactoryReferenceAttribute<RestClient,RestClientFactory<V>>().setupUnsafe(RestClientFactory.class);//.en("rest client");
    public final ObjectValueAttribute<Class<T>> factoryRootClass = new ObjectValueAttribute<>();//.en("factoryRootClass");
    public final StringAttribute user = new StringAttribute().en("user");
    public final StringAttribute passwordHash = new StringAttribute().en("passwordHash");

    @Override
    public ApplicationServerRestClient<V, T> createImpl() {
        return new ApplicationServerRestClient<>(restClient.instance(),factoryRootClass.get(),user.get(),passwordHash.get());
    }

}
