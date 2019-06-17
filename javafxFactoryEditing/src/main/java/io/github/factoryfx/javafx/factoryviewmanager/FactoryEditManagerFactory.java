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
 * @param <S> History summary
 */
public class FactoryEditManagerFactory<RS  extends FactoryBase<?,RS>,S> extends SimpleFactoryBase<FactoryEditManager<RS,S>,RichClientRoot> {

    public final FactoryAttribute<RichClientRoot,MicroserviceRestClient<RS,S>, MicroserviceRestClientFactory<RichClientRoot,RS,S>> restClient = new FactoryAttribute<>();
    public final FactoryAttribute<RichClientRoot,MigrationManager<RS,S>,FactorySerialisationManagerFactory<RS,S>> factorySerialisationManager = new FactoryAttribute<>();

    @Override
    protected FactoryEditManager<RS,S> createImpl() {
        return new FactoryEditManager<>(restClient.instance(),factorySerialisationManager.instance());
    }


}
