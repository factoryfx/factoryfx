package de.factoryfx.server.rest.client;

import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.types.ObjectValueAttribute;
import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;

public class ApplicationServerRestClientFactory<V,T extends FactoryBase<?,V>> extends SimpleFactoryBase<ApplicationServerRestClient<V,T>,V> {
    public final FactoryReferenceAttribute<RestClient,RestClientFactory<V>> restClient= new FactoryReferenceAttribute<>(new AttributeMetadata().en("rest client"),RestClientFactory.class);
    public final ObjectValueAttribute<Class<T>> factoryRootClass = new ObjectValueAttribute<>(new AttributeMetadata().en("factoryRootClass"));
    public final StringAttribute user = new StringAttribute(new AttributeMetadata().en("user"));
    public final StringAttribute passwordHash = new StringAttribute(new AttributeMetadata().en("passwordHash"));

    @Override
    public ApplicationServerRestClient<V, T> createImpl() {
        return new ApplicationServerRestClient<>(restClient.instance(),factoryRootClass.get(),user.get(),passwordHash.get());
    }

}
