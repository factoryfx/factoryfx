package de.factoryfx.jetty;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.factory.atrribute.FactoryReferenceListAttribute;
import org.glassfish.jersey.logging.LoggingFeature;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 *   Unusual inheritance api to support type-safe navigation.
 *  (alternative would be a factorylist but there is no common interface for resources)
 *
 *  usage example.
 *
 *  <pre>{@code
 *      public static class TestWebserverFactory extends JettyServerFactory<Void,RootFactory>{
 *          public final FactoryReferenceAttribute<Resource1,Resource1FactoryBase> resource = new FactoryReferenceAttribute<>(Resource1FactoryBase.class);
 *          Override
 *          protected List<Object> getResourcesInstances() {
 *              return List.of(resource.instance());
 *          }
 *      }
 *  }</pre>
 */
public abstract class JettyServerFactory<V,R extends FactoryBase<?,V,R>> extends FactoryBase<JettyServer,V,R> {

    /** jersey resource class with Annotations*/
//    public final FactoryReferenceListAttribute<Object,FactoryBase<?,V>> resources = new FactoryReferenceListAttribute<Object,FactoryBase<?,V>>().setupUnsafe(FactoryBase.class).labelText("resource");
    @SuppressWarnings("unchecked")
    public final FactoryReferenceListAttribute<HttpServerConnectorCreator,HttpServerConnectorFactory<V,R>> connectors =
            FactoryReferenceListAttribute.create( new FactoryReferenceListAttribute<>(HttpServerConnectorFactory.class).labelText("Connectors").userNotSelectable());
    @SuppressWarnings("unchecked")
    public final FactoryReferenceAttribute<ObjectMapper,FactoryBase<ObjectMapper,V,R>> objectMapper =
            FactoryReferenceAttribute.create( new FactoryReferenceAttribute<>(FactoryBase.class).nullable().en("objectMapper"));
    @SuppressWarnings("unchecked")
    public final FactoryReferenceAttribute<org.glassfish.jersey.logging.LoggingFeature,FactoryBase<org.glassfish.jersey.logging.LoggingFeature,V,R>> restLogging =
            FactoryReferenceAttribute.create(new FactoryReferenceAttribute<>(FactoryBase.class).userReadOnly().nullable().labelText("REST logging"));


    public JettyServerFactory(){
        configLifeCycle().setCreator(this::createJetty);
        configLifeCycle().setReCreator(currentLiveObject->{
            ServletBuilder servletBuilder = new ServletBuilder();
            setupServlets(servletBuilder);
            return currentLiveObject.recreate(connectors.instances(), servletBuilder);
        });

        configLifeCycle().setStarter(JettyServer::start);
        configLifeCycle().setDestroyer(JettyServer::stop);

        config().setDisplayTextProvider(() -> "Microservice REST server");
    }

    //api for customizing JettyServer creation
    protected JettyServer createJetty() {
        ServletBuilder servletBuilder = new ServletBuilder();
        setupServlets(servletBuilder);
        return new JettyServer(connectors.instances(), servletBuilder);
    }

    /**
     * When migrating from older revision you can simple call {@link #defaultSetupServlets(ServletBuilder, List)} to achieve the old beghaviour
     * @param servletBuilder
     */
    protected abstract void setupServlets(ServletBuilder servletBuilder);

    protected final void defaultSetupServlets(ServletBuilder servletBuilder, Object ... resources) {
        defaultSetupServlets(servletBuilder, Arrays.asList(resources));
    }

     protected final void defaultSetupServlets(ServletBuilder servletBuilder, List<Object> resources) {
        LoggingFeature lf = restLogging.instance();
        if (lf == null) {
            servletBuilder.withDefaultJerseyLoggingFeature();
        } else {
            servletBuilder.withJerseyResource(lf);
        }
        servletBuilder.withDefaultJerseyAllExceptionMapper();
        List<Object> noNulls = resources.stream().filter(o->o != null).collect(Collectors.toList());
        if (!noNulls.isEmpty()) {
            ObjectMapper objectMapper = this.objectMapper.instance();
            if (objectMapper != null)
                servletBuilder.withJerseyJacksonObjectMapper(objectMapper);
            servletBuilder.withJerseyResources("/*",resources);
        }
    }


}
