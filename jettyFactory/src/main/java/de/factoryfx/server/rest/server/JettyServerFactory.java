package de.factoryfx.server.rest.server;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.factory.atrribute.FactoryReferenceListAttribute;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 *   Unusual inheritance api to support type-safe navigation.
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
    public final FactoryReferenceListAttribute<HttpServerConnectorCreator,HttpServerConnectorFactory<V>> connectors = new FactoryReferenceListAttribute<HttpServerConnectorCreator,HttpServerConnectorFactory<V>>().setupUnsafe(HttpServerConnectorFactory.class).labelText("connectors").userNotSelectable();
    public final FactoryReferenceAttribute<ObjectMapper,FactoryBase<ObjectMapper,V>> objectMapper = new FactoryReferenceAttribute<ObjectMapper,FactoryBase<ObjectMapper,V>>().setupUnsafe(FactoryBase.class).labelText("connectors").userReadOnly();



    public JettyServerFactory(){
        configLiveCycle().setCreator(() -> {
            return new JettyServer(connectors.instances(), getResourcesInstancesNullRemoved(),objectMapper.instance());
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
