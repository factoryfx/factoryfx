package de.factoryfx.jetty;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import de.factoryfx.data.jackson.ObjectMapperBuilder;
import org.eclipse.jetty.http.pathmap.ServletPathSpec;
import org.glassfish.jersey.CommonProperties;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

import javax.servlet.Servlet;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class ServletBuilder {

    static final ObjectMapper DEFAULT_OBJECT_MAPPER = ObjectMapperBuilder.buildNewObjectMapper();

    final List<Object> additionalJerseyResources = new ArrayList<>();
    final List<ServletAndPathSpec> mappings = new ArrayList<>();
    ObjectMapper objectMapper = DEFAULT_OBJECT_MAPPER;

    public void forEachServletMapping(BiConsumer<ServletPathSpec, Servlet> consumer) {
        mappings.forEach(m->{
            consumer.accept(m.servletPathSpec,m.servlet);
        });
    }

    public ServletBuilder withServlet(String pathSpec, Servlet servlet) {
        mappings.add(new ServletAndPathSpec(new ServletPathSpec(pathSpec),servlet));
        return this;
    }

    public ServletBuilder withJerseyResources(String pathSpec, List<Object> resources) {
        mappings.add(new ServletAndPathSpec(new ServletPathSpec(pathSpec),createJerseyServlet(resources)));
        return this;
    }

    public ServletBuilder withJerseyJacksonObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        return this;
    }

    public ServletBuilder withDefaultJerseyAllExceptionMapper() {
        return withJerseyResource(new AllExceptionMapper());
    }

    public ServletBuilder withDefaultJerseyLoggingFeature() {
        return withJerseyResource(new org.glassfish.jersey.logging.LoggingFeature(new DelegatingLoggingFilterLogger()));
    }

    public ServletBuilder withJerseyResource(Object resource) {
        if (resource != null) {
            additionalJerseyResources.add(resource);
        }
        return this;
    }

    public Servlet createServletFromResourceConfig(ResourceConfig resourceConfig) {
        return new ServletContainer(resourceConfig);
    }

    public Servlet createJerseyServlet(List<Object> resource) {
        return createServletFromResourceConfig(jerseySetup(resource));
    }

    private ResourceConfig jerseySetup(List<Object> resource) {

        ResourceConfig resourceConfig = new ResourceConfig();
        resourceConfig.property(CommonProperties.FEATURE_AUTO_DISCOVERY_DISABLE, true);// without we have 2 JacksonJaxbJsonProvider and wrong mapper
        resource.forEach(resourceConfig::register);

        JacksonJaxbJsonProvider provider = new JacksonJaxbJsonProvider();
        provider.setMapper(objectMapper);
        resourceConfig.register(provider);

        additionalJerseyResources.forEach(r->{
            if (r instanceof Class) {
                resourceConfig.register((Class)r);
            } else {
                resourceConfig.register(r);
            }
        });

        return resourceConfig;
    }

    static class ServletAndPathSpec {
        final ServletPathSpec servletPathSpec;
        final Servlet servlet;

        ServletAndPathSpec(ServletPathSpec servletPathSpec, Servlet servlet) {
            this.servletPathSpec = servletPathSpec;
            this.servlet = servlet;
        }
    }

}
