package de.factoryfx.javafx.factory.view.factoryviewmanager;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.data.storage.migration.MigrationManager;
import de.factoryfx.javafx.factory.RichClientRoot;
import de.factoryfx.microservice.rest.client.MicroserviceRestClient;
import de.factoryfx.microservice.rest.client.MicroserviceRestClientFactory;

/**
 *
 * @param <RS> server root
 * @param <S> History summary
 */
public class FactoryEditManagerFactory<RS  extends FactoryBase<?,RS>,S> extends SimpleFactoryBase<FactoryEditManager<RS,S>,RichClientRoot> {

    @SuppressWarnings("unchecked")
    public final FactoryReferenceAttribute<MicroserviceRestClient<RS,S>,MicroserviceRestClientFactory<RichClientRoot,RS,S>> restClient =
            FactoryReferenceAttribute.create(new FactoryReferenceAttribute<>(MicroserviceRestClientFactory.class));
    @SuppressWarnings("unchecked")
    public final FactoryReferenceAttribute<MigrationManager<RS,S>,FactorySerialisationManagerFactory<RS,S>> factorySerialisationManager =
            FactoryReferenceAttribute.create(new FactoryReferenceAttribute<>(FactorySerialisationManagerFactory.class));

    @Override
    public FactoryEditManager<RS,S> createImpl() {
        return new FactoryEditManager<>(restClient.instance(),factorySerialisationManager.instance());
    }


}
