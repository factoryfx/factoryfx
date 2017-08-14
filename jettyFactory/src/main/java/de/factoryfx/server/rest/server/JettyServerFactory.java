package de.factoryfx.server.rest.server;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceListAttribute;

public class JettyServerFactory<V> extends FactoryBase<JettyServer,V> {

    /** jersey resource class with Annotations*/
    public final FactoryReferenceListAttribute<Object,FactoryBase<?,V>> resources = (FactoryReferenceListAttribute)new FactoryReferenceListAttribute<>(FactoryBase.class).labelText("resource");
    public final FactoryReferenceListAttribute<HttpServerConnectorCreator,HttpServerConnectorFactory<V>> connectors = (FactoryReferenceListAttribute)new FactoryReferenceListAttribute<>(HttpServerConnectorFactory.class).labelText("connectors");

    public JettyServerFactory(){
        configLiveCycle().setCreator(() -> new JettyServer(connectors.instances(), resources.instances()));
        configLiveCycle().setReCreator(currentLiveObject->currentLiveObject.recreate(connectors.instances(),resources.instances()));

        configLiveCycle().setStarter(JettyServer::start);
        configLiveCycle().setDestroyer(JettyServer::stop);

        config().setDisplayTextProvider(() -> "ApplicationServerRestServer");
    }
}
