package de.factoryfx.javafx.view.factoryviewmanager;

import de.factoryfx.data.attribute.types.ObjectValueAttribute;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.data.storage.DataSerialisationManager;
import de.factoryfx.server.rest.client.ApplicationServerRestClient;
import de.factoryfx.server.rest.client.ApplicationServerRestClientFactory;

/**
 *
 * @param <V> client visitor
 * @param <R> client root
 * @param <VS> server visitor
 * @param <RS> server root
 */
public class FactoryEditManagerFactory<V,R  extends FactoryBase<?,V>,VS,RS  extends FactoryBase<?,VS>> extends SimpleFactoryBase<FactoryEditManager<VS,RS>,Void> {

    public final FactoryReferenceAttribute<ApplicationServerRestClient<VS,RS>,ApplicationServerRestClientFactory<V,R,VS,RS>> restClient = new FactoryReferenceAttribute<ApplicationServerRestClient<VS,RS>,ApplicationServerRestClientFactory<V,R,VS,RS>>().setupUnsafe(ApplicationServerRestClientFactory.class);
    //TODO refactor to FactoryReferenceAttribute?
    public final ObjectValueAttribute<DataSerialisationManager<RS>> factorySerialisationManager = new ObjectValueAttribute<>();


    @Override
    public FactoryEditManager<VS,RS> createImpl() {
        return new FactoryEditManager<>(restClient.instance(),factorySerialisationManager.get());
    }


}
