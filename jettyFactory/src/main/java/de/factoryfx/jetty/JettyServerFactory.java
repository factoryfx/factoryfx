package de.factoryfx.jetty;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.factory.atrribute.FactoryReferenceListAttribute;

/**
 *   Unusual inheritance api to support type-safe navigation.
 *  (alternative would be a factorylist but there is no common interface for resources)
 *
 *  usage example.
 *
 *  <pre>{@code
 *      public static class TestWebserverFactory extends JettyServerFactory<Void>{
 *          public final FactoryReferenceAttribute<Resource1,Resource1FactoryBase> resource = new FactoryReferenceAttribute<>(Resource1FactoryBase.class);
 *          Override
 *          protected List<Object> getResourcesInstances() {
 *              return Arrays.asList(resource.instance());
 *          }
 *      }
 *  }</pre>
 */
public abstract class JettyServerFactory<V,R extends FactoryBase<?,V,R>> extends FactoryBase<JettyServer,V,R> {

    /** jersey resource class with Annotations*/
//    public final FactoryReferenceListAttribute<Object,FactoryBase<?,V>> resources = new FactoryReferenceListAttribute<Object,FactoryBase<?,V>>().setupUnsafe(FactoryBase.class).labelText("resource");
    public final FactoryReferenceListAttribute<HttpServerConnectorCreator,HttpServerConnectorFactory<V,R>> connectors = new FactoryReferenceListAttribute<HttpServerConnectorCreator,HttpServerConnectorFactory<V,R>>().setupUnsafe(HttpServerConnectorFactory.class).labelText("connectors").userNotSelectable();
    public final FactoryReferenceAttribute<ObjectMapper,FactoryBase<ObjectMapper,V,R>> objectMapper = new FactoryReferenceAttribute<ObjectMapper,FactoryBase<ObjectMapper,V,R>>().setupUnsafe(FactoryBase.class).labelText("object mapper").userReadOnly().nullable();
    public final FactoryReferenceAttribute<org.glassfish.jersey.logging.LoggingFeature,FactoryBase<org.glassfish.jersey.logging.LoggingFeature,V,R>> restLogging = new FactoryReferenceAttribute<org.glassfish.jersey.logging.LoggingFeature,FactoryBase<org.glassfish.jersey.logging.LoggingFeature,V,R>>().setupUnsafe(FactoryBase.class).labelText("restLogging").userReadOnly().nullable();


    public JettyServerFactory(){
        configLiveCycle().setCreator(this::createJetty);
        configLiveCycle().setReCreator(currentLiveObject->currentLiveObject.recreate(connectors.instances(), getResourcesInstancesNullRemoved()));

        configLiveCycle().setStarter(JettyServer::start);
        configLiveCycle().setDestroyer(JettyServer::stop);

        config().setDisplayTextProvider(() -> "MicroserviceRestServer");
    }

    //api for customizing JettyServer creation
    protected JettyServer createJetty() {
        return new JettyServer(connectors.instances(), getResourcesInstancesNullRemoved(), objectMapper.instance(),restLogging.instance());
    }

    protected List<Object> getResourcesInstancesNullRemoved(){
        return getResourcesInstances().stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    /**
     * @return jersey resource class with Annotation
     * */
    @JsonIgnore
    protected abstract List<Object> getResourcesInstances();
}
