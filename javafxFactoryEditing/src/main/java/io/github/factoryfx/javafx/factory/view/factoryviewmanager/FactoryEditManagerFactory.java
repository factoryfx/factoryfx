package io.github.factoryfx.javafx.factory.view.factoryviewmanager;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import io.github.factoryfx.data.storage.migration.MigrationManager;
import io.github.factoryfx.javafx.factory.RichClientRoot;
import io.github.factoryfx.microservice.rest.client.MicroserviceRestClient;
import io.github.factoryfx.microservice.rest.client.MicroserviceRestClientFactory;

/**
 *
 * @param <RS> server root
 * @param <S> History summary
 */
public class FactoryEditManagerFactory<RS  extends FactoryBase<?,RS>,S> extends SimpleFactoryBase<FactoryEditManager<RS,S>,RichClientRoot> {

    @SuppressWarnings("unchecked")
    public final FactoryReferenceAttribute<MicroserviceRestClient<RS,S>, MicroserviceRestClientFactory<RichClientRoot,RS,S>> restClient =
            FactoryReferenceAttribute.create(new FactoryReferenceAttribute<>(MicroserviceRestClientFactory.class));
    @SuppressWarnings("unchecked")
    public final FactoryReferenceAttribute<MigrationManager<RS,S>,FactorySerialisationManagerFactory<RS,S>> factorySerialisationManager =
            FactoryReferenceAttribute.create(new FactoryReferenceAttribute<>(FactorySerialisationManagerFactory.class));

    @Override
    public FactoryEditManager<RS,S> createImpl() {
        return new FactoryEditManager<>(restClient.instance(),factorySerialisationManager.instance());
    }


}
