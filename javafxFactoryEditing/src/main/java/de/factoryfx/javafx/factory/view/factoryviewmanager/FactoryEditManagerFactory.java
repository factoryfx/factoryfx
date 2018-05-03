package de.factoryfx.javafx.factory.view.factoryviewmanager;

import de.factoryfx.data.attribute.types.ObjectValueAttribute;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.data.storage.DataSerialisationManager;
import de.factoryfx.microservice.rest.client.MicroserviceRestClient;
import de.factoryfx.microservice.rest.client.MicroserviceRestClientFactory;

/**
 *
 * @param <V> client visitor
 * @param <R> client root
 * @param <VS> server visitor
 * @param <RS> server root
 */
public class FactoryEditManagerFactory<V,R  extends FactoryBase<?,V,R>,VS,RS  extends FactoryBase<?,VS,RS>,S> extends SimpleFactoryBase<FactoryEditManager<VS,RS,S>,V,R> {

    public final FactoryReferenceAttribute<MicroserviceRestClient<VS,RS>,MicroserviceRestClientFactory<V,R,VS,RS>> restClient = new FactoryReferenceAttribute<MicroserviceRestClient<VS,RS>,MicroserviceRestClientFactory<V,R,VS,RS>>().setupUnsafe(MicroserviceRestClientFactory.class);
    //TODO refactor to FactoryReferenceAttribute?
    public final ObjectValueAttribute<DataSerialisationManager<RS,S>> factorySerialisationManager = new ObjectValueAttribute<>();


    @Override
    public FactoryEditManager<VS,RS,S> createImpl() {
        return new FactoryEditManager<VS,RS,S>(restClient.instance(),factorySerialisationManager.get());
    }


}
