package io.github.factoryfx.jetty;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.AttributeCopy;
import io.github.factoryfx.factory.attribute.ValueMapAttribute;
import io.github.factoryfx.factory.attribute.dependency.FactoryPolymorphicAttribute;
import io.github.factoryfx.factory.attribute.dependency.FactoryPolymorphicListAttribute;
import io.github.factoryfx.factory.attribute.types.BooleanMapAttribute;
import io.github.factoryfx.factory.attribute.types.StringMapAttribute;
import org.glassfish.jersey.CommonProperties;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

import javax.servlet.*;
import javax.ws.rs.ext.ExceptionMapper;
import java.util.*;
import java.util.function.Consumer;

public class JerseyServletFactory<R extends FactoryBase<?,R>> extends SimpleFactoryBase<Servlet,R> {

    public final FactoryPolymorphicAttribute<ObjectMapper> objectMapper = new FactoryPolymorphicAttribute<ObjectMapper>().en("objectMapper");
    public final FactoryPolymorphicAttribute<LoggingFeature> restLogging = new FactoryPolymorphicAttribute<LoggingFeature>().userReadOnly().labelText("REST logging");
    public final FactoryPolymorphicListAttribute<Object> additionalJaxrsComponents = new FactoryPolymorphicListAttribute<>().userReadOnly().labelText("additionalJaxrsComponents");
    public final FactoryPolymorphicListAttribute<Object> resources = new FactoryPolymorphicListAttribute<>().labelText("resources");
    public final FactoryPolymorphicAttribute<ExceptionMapper<Throwable>> exceptionMapper = new FactoryPolymorphicAttribute<ExceptionMapper<Throwable>>().userReadOnly().labelText("exceptionMapper");
    public final BooleanMapAttribute jerseyProperties= new BooleanMapAttribute();

    public JerseyServletFactory(){
        this.configLifeCycle().setUpdater(servlet -> ((ServletContainer)servlet).reload(createResourceConfig()));
    }

    @Override
    protected Servlet createImpl() {
        return new ServletContainer(createResourceConfig());
    }

    private ResourceConfig createResourceConfig() {
        ResourceConfig resourceConfig = new ResourceConfig();
        resourceConfig.property(CommonProperties.FEATURE_AUTO_DISCOVERY_DISABLE, true);// without we have 2 JacksonJaxbJsonProvider and wrong mapper
//        resourceConfig.property(ServerProperties.BV_FEATURE_DISABLE, true);
//        resourceConfig.property(ServerProperties.RESOURCE_VALIDATION_DISABLE, true);
        for (Map.Entry<String, Boolean> entry: jerseyProperties.entrySet()) {
            resourceConfig.property(entry.getKey(),entry.getValue());
        }
        getResourcesInstances().forEach(resourceConfig::register);

        JacksonJaxbJsonProvider provider = new JacksonJaxbJsonProvider();
        provider.setMapper(objectMapper.instance());
        resourceConfig.register(provider);

        resourceConfig.register(restLogging.instance());

        resourceConfig.register(exceptionMapper.instance());


        additionalJaxrsComponents.instances().forEach(r -> {
            if (r instanceof Class) {
                resourceConfig.register((Class<?>) r);
            } else {
                resourceConfig.register(r);
            }
        });
        return resourceConfig;
    }

    protected List<Object> getResourcesInstances(){
        return resources.instances();
    }


}
