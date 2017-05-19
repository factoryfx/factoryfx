package de.factoryfx.server.rest.server;

import java.util.function.Function;

import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceListAttribute;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;

public class JettyServerFactory<V> extends FactoryBase<JettyServer,V> {

    /** jersey resource class with Annotations*/
    public final FactoryReferenceListAttribute<Object,FactoryBase<?,V>> resources = new FactoryReferenceListAttribute<>(new AttributeMetadata().labelText("resource"),Object.class);
    public final FactoryReferenceListAttribute<HttpServerConnectorCreator,HttpServerConnectorFactory<V>> connectors = new FactoryReferenceListAttribute<>(new AttributeMetadata().labelText("connectors"),HttpServerConnectorFactory.class);

    public JettyServerFactory(){
        configLiveCycle().setCreator(() -> {
            return new JettyServer(connectors.instances(), resources.instances());
        });
        configLiveCycle().setReCreator(currentLiveObject->currentLiveObject.recreate(connectors.instances(),resources.instances()));

        configLiveCycle().setStarter(newLiveObject -> newLiveObject.start());
        configLiveCycle().setDestroyer(previousLiveObject -> previousLiveObject.stop());

        config().setDisplayTextProvider(() -> "ApplicationServerRestServer");
    }
}
