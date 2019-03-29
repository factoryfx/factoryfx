package io.github.factoryfx.javafx.factory.factoryviewmanager;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryReferenceAttribute;
import io.github.factoryfx.factory.storage.migration.MigrationManager;
import io.github.factoryfx.javafx.factory.RichClientRoot;
import io.github.factoryfx.microservice.rest.client.MicroserviceRestClient;
import io.github.factoryfx.microservice.rest.client.MicroserviceRestClientFactory;

/**
 *
 * @param <RS> server root
 * @param <S> History summary
 */
public class FactoryEditManagerFactory<RS  extends FactoryBase<?,RS>,S> extends SimpleFactoryBase<FactoryEditManager<RS,S>,RichClientRoot> {

    public final FactoryReferenceAttribute<RichClientRoot,MicroserviceRestClient<RS,S>, MicroserviceRestClientFactory<RichClientRoot,RS,S>> restClient = new FactoryReferenceAttribute<>();
    public final FactoryReferenceAttribute<RichClientRoot,MigrationManager<RS,S>,FactorySerialisationManagerFactory<RS,S>> factorySerialisationManager = new FactoryReferenceAttribute<>();

    @Override
    public FactoryEditManager<RS,S> createImpl() {
        return new FactoryEditManager<>(restClient.instance(),factorySerialisationManager.instance());
    }


}
