package io.github.factoryfx.javafx.factoryviewmanager;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;
import io.github.factoryfx.factory.storage.migration.MigrationManager;
import io.github.factoryfx.javafx.RichClientRoot;
import io.github.factoryfx.microservice.rest.client.MicroserviceRestClient;
import io.github.factoryfx.microservice.rest.client.MicroserviceRestClientFactory;

/**
 *
 * @param <RS> server root
 */
public class FactoryEditManagerFactory<RS  extends FactoryBase<?,RS>> extends SimpleFactoryBase<FactoryEditManager<RS>,RichClientRoot> {

    public final FactoryAttribute<RichClientRoot,MicroserviceRestClient<RS>, MicroserviceRestClientFactory<RichClientRoot,RS>> restClient = new FactoryAttribute<>();
    public final FactoryAttribute<RichClientRoot,MigrationManager<RS>,FactorySerialisationManagerFactory<RS>> factorySerialisationManager = new FactoryAttribute<>();

    @Override
    protected FactoryEditManager<RS> createImpl() {
        return new FactoryEditManager<>(restClient.instance(),factorySerialisationManager.instance());
    }


}
