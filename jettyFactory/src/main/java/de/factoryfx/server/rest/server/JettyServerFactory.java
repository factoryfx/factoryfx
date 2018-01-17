package de.factoryfx.server.rest.server;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.factoryfx.data.attribute.types.ObjectValueAttribute;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceListAttribute;
import org.eclipse.jetty.server.Handler;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 *   Unusual inheritance api to support typesafe navigation.
 *  (alternative would be a factorylist but there is no common interface for resources)
 *
 *  usage example.
 *
 *  {@code
 *      public static class TestWebserverFactory extends JettyServerFactory<Void>{
 *          public final FactoryReferenceAttribute<Resource1,Resource1FactoryBase> resource = new FactoryReferenceAttribute<>(Resource1FactoryBase.class);
 *          @Override
 *          protected List<Object> getResourcesInstances() {
 *              return Arrays.asList(resource.instance());
 *          }
 *      }
 *  }
 */
public abstract class JettyServerFactory<V> extends FactoryBase<JettyServer,V> {

    /** jersey resource class with Annotations*/
//    public final FactoryReferenceListAttribute<Object,FactoryBase<?,V>> resources = new FactoryReferenceListAttribute<Object,FactoryBase<?,V>>().setupUnsafe(FactoryBase.class).labelText("resource");
    public final FactoryReferenceListAttribute<HttpServerConnectorCreator,HttpServerConnectorFactory<V>> connectors = new FactoryReferenceListAttribute<HttpServerConnectorCreator,HttpServerConnectorFactory<V>>().setupUnsafe(HttpServerConnectorFactory.class).labelText("connectors");

    public JettyServerFactory(){
        configLiveCycle().setCreator(() -> {
            return new JettyServer(connectors.instances(), getResourcesInstancesNullRemoved());
        });
        configLiveCycle().setReCreator(currentLiveObject->currentLiveObject.recreate(connectors.instances(), getResourcesInstancesNullRemoved()));

        configLiveCycle().setStarter(JettyServer::start);
        configLiveCycle().setDestroyer(JettyServer::stop);

        config().setDisplayTextProvider(() -> "ApplicationServerRestServer");
    }

    private List<Object> getResourcesInstancesNullRemoved(){
        return getResourcesInstances().stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    /**
     * @return jersey resource class with Annotation
     * */
    @JsonIgnore
    protected abstract List<Object> getResourcesInstances();
}
