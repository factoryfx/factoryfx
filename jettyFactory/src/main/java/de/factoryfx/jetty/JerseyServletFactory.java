package de.factoryfx.jetty;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import de.factoryfx.data.attribute.types.ObjectValueAttribute;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryPolymorphicReferenceAttribute;
import de.factoryfx.factory.atrribute.FactoryPolymorphicReferenceListAttribute;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import org.glassfish.jersey.CommonProperties;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

import javax.servlet.*;
import java.util.*;

public class JerseyServletFactory<R extends FactoryBase<?,R>> extends SimpleFactoryBase<Servlet,R> {

    @SuppressWarnings("unchecked")
    public final FactoryReferenceAttribute<ObjectMapper,FactoryBase<ObjectMapper,R>> objectMapper =
            FactoryReferenceAttribute.create( new FactoryReferenceAttribute<>(FactoryBase.class).nullable().en("objectMapper"));

    public final FactoryPolymorphicReferenceAttribute<LoggingFeature> restLogging = new FactoryPolymorphicReferenceAttribute<>(LoggingFeature.class).userReadOnly().labelText("REST logging");

    public final ObjectValueAttribute<List<Object>> additionalJaxrsComponents = new ObjectValueAttribute<List<Object>>().userReadOnly().labelText("additionalJaxrsComponents").nullable();


    public final FactoryPolymorphicReferenceListAttribute<Object> resources = new FactoryPolymorphicReferenceListAttribute<>(Object.class).labelText("resources");

    @Override
    public Servlet createImpl() {
        ResourceConfig resourceConfig = new ResourceConfig();
        resourceConfig.property(CommonProperties.FEATURE_AUTO_DISCOVERY_DISABLE, true);// without we have 2 JacksonJaxbJsonProvider and wrong mapper
//        resourceConfig.property(ServerProperties.BV_FEATURE_DISABLE, true);
//        resourceConfig.property(ServerProperties.RESOURCE_VALIDATION_DISABLE, true);
        getResourcesInstances().forEach(resourceConfig::register);

        JacksonJaxbJsonProvider provider = new JacksonJaxbJsonProvider();
        provider.setMapper(objectMapper.instance());
        resourceConfig.register(provider);

        resourceConfig.register(restLogging.instance());

        resourceConfig.register(new AllExceptionMapper());

        if (additionalJaxrsComponents.get()!=null){
            additionalJaxrsComponents.get().forEach(r -> {
                if (r instanceof Class) {
                    resourceConfig.register((Class) r);
                } else {
                    resourceConfig.register(r);
                }
            });
        }

        return new ServletContainer(resourceConfig);
    }

    protected List<Object> getResourcesInstances(){
        return resources.instances();
    }


}
