package de.factoryfx.server.rest;

import java.util.function.Function;

import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.factory.atrribute.FactoryReferenceListAttribute;
import de.factoryfx.server.rest.server.HttpServerConnectorFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;

public class ApplicationServerRestServerFactory<V,L,T extends FactoryBase<L,V>> extends FactoryBase<ApplicationServerRestServer<V,L,T>,V> {

    public final StringAttribute contentPath = new StringAttribute(new AttributeMetadata().labelText("context path")).defaultValue("/applicationServer/*");
    public final FactoryReferenceAttribute<ApplicationServerResource,ApplicationServerResourceFactory<V,L,T>> applicationServerResource = new FactoryReferenceAttribute<>(new AttributeMetadata().labelText("resource"),ApplicationServerResourceFactory.class);
    public final FactoryReferenceListAttribute<Function<Server,ServerConnector>,HttpServerConnectorFactory<V>> connectors = new FactoryReferenceListAttribute<>(new AttributeMetadata().labelText("connectors"),HttpServerConnectorFactory.class);

    public ApplicationServerRestServerFactory(){
        configLiveCycle().setCreator(() -> {
            return new ApplicationServerRestServer<V,L,T>(applicationServerResource.instance(), connectors.instances(), contentPath.get());
        });

        configLiveCycle().setStarter(newLiveObject -> newLiveObject.start());
        configLiveCycle().setDestroyer(previousLiveObject -> previousLiveObject.stop());

        config().setDisplayTextProvider(() -> "ApplicationServerRestServer");
    }
}
