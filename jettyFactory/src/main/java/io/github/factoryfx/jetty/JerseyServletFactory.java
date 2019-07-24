package io.github.factoryfx.jetty;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import io.github.factoryfx.factory.attribute.types.ObjectValueAttribute;
import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryPolymorphicAttribute;
import io.github.factoryfx.factory.attribute.dependency.FactoryPolymorphicListAttribute;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;
import org.glassfish.jersey.CommonProperties;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

import javax.servlet.*;
import javax.ws.rs.ext.ExceptionMapper;
import java.util.*;

public class JerseyServletFactory<R extends FactoryBase<?,R>> extends SimpleFactoryBase<Servlet,R> {

    public final FactoryPolymorphicAttribute<R,ObjectMapper> objectMapper = new FactoryPolymorphicAttribute<R,ObjectMapper>().nullable().en("objectMapper");
    public final FactoryPolymorphicAttribute<R,LoggingFeature> restLogging = new FactoryPolymorphicAttribute<R,LoggingFeature>().userReadOnly().labelText("REST logging");
    public final FactoryPolymorphicListAttribute<R,Object> additionalJaxrsComponents = new FactoryPolymorphicListAttribute<R,Object>().userReadOnly().labelText("additionalJaxrsComponents");
    public final FactoryPolymorphicListAttribute<R,Object> resources = new FactoryPolymorphicListAttribute<R,Object>().labelText("resources");
    public final FactoryPolymorphicAttribute<R,ExceptionMapper<Throwable>> exceptionMapper = new FactoryPolymorphicAttribute<R,ExceptionMapper<Throwable>>().userReadOnly().labelText("exceptionMapper").nullable();

    @Override
    protected Servlet createImpl() {
        ResourceConfig resourceConfig = new ResourceConfig();
        resourceConfig.property(CommonProperties.FEATURE_AUTO_DISCOVERY_DISABLE, true);// without we have 2 JacksonJaxbJsonProvider and wrong mapper
//        resourceConfig.property(ServerProperties.BV_FEATURE_DISABLE, true);
//        resourceConfig.property(ServerProperties.RESOURCE_VALIDATION_DISABLE, true);
        getResourcesInstances().forEach(resourceConfig::register);

        JacksonJaxbJsonProvider provider = new JacksonJaxbJsonProvider();
        provider.setMapper(objectMapper.instance());
        resourceConfig.register(provider);

        resourceConfig.register(restLogging.instance());

        resourceConfig.register(Optional.ofNullable(exceptionMapper.instance()).orElse(new AllExceptionMapper()));


        additionalJaxrsComponents.instances().forEach(r -> {
            if (r instanceof Class) {
                resourceConfig.register((Class) r);
            } else {
                resourceConfig.register(r);
            }
        });


        return new ServletContainer(resourceConfig);
    }

    protected List<Object> getResourcesInstances(){
        return resources.instances();
    }


}
