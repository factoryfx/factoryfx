package de.factoryfx.javafx.factory.view.factoryviewmanager;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.data.storage.DataSerialisationManager;
import de.factoryfx.javafx.factory.RichClientRoot;
import de.factoryfx.microservice.rest.client.MicroserviceRestClient;
import de.factoryfx.microservice.rest.client.MicroserviceRestClientFactory;

/**
 *
 * @param <VS> server visitor
 * @param <RS> server root
 * @param <S> History summary
 */
public class FactoryEditManagerFactory<VS,RS  extends FactoryBase<?,VS,RS>,S> extends SimpleFactoryBase<FactoryEditManager<VS,RS>,Void,RichClientRoot> {

    public final FactoryReferenceAttribute<MicroserviceRestClient<VS,RS,S>,MicroserviceRestClientFactory<Void,RichClientRoot,VS,RS,S>> restClient = new FactoryReferenceAttribute<MicroserviceRestClient<VS,RS,S>,MicroserviceRestClientFactory<Void,RichClientRoot,VS,RS,S>>().setupUnsafe(MicroserviceRestClientFactory.class);
    public final FactoryReferenceAttribute<DataSerialisationManager<RS,S>,FactorySerialisationManagerFactory<RS,S>> factorySerialisationManager = new FactoryReferenceAttribute<DataSerialisationManager<RS,S>,FactorySerialisationManagerFactory<RS,S>>().setupUnsafe(FactorySerialisationManagerFactory.class);

    @Override
    public FactoryEditManager<VS,RS> createImpl() {
        return new FactoryEditManager<>(restClient.instance(),factorySerialisationManager.instance());
    }


}
