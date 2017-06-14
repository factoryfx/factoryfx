package de.factoryfx.javafx.view.factoryviewmanager;

import de.factoryfx.data.attribute.types.ObjectValueAttribute;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.factory.datastorage.FactorySerialisationManager;
import de.factoryfx.server.rest.client.ApplicationServerRestClient;
import de.factoryfx.server.rest.client.ApplicationServerRestClientFactory;

/**
 *
 * @param <V> server visitor
 * @param <R> server root
 */
public class FactoryEditManagerFactory<V,R  extends FactoryBase<?,V>> extends SimpleFactoryBase<FactoryEditManager<V,R>,Void> {

    public final FactoryReferenceAttribute<ApplicationServerRestClient<V,R>,ApplicationServerRestClientFactory<V,R>> restClient = new FactoryReferenceAttribute<ApplicationServerRestClient<V,R>,ApplicationServerRestClientFactory<V,R>>().setupUnsafe(ApplicationServerRestClientFactory.class).de("restClient").en("restClient");
    //TODO refactor to FactoryReferenceAttribute?
    public final ObjectValueAttribute<FactorySerialisationManager<R>> factorySerialisationManager = new ObjectValueAttribute<FactorySerialisationManager<R>>().de("factorySerialisationManager").en("factorySerialisationManager");


    @Override
    public FactoryEditManager<V,R> createImpl() {
        return new FactoryEditManager<>(restClient.instance(),factorySerialisationManager.get());
    }


}
