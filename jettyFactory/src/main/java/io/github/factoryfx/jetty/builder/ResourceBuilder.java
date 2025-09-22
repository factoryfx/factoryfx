package io.github.factoryfx.jetty.builder;

import java.util.*;
import java.util.function.Function;

import org.glassfish.jersey.logging.LoggingFeature;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.factoryfx.factory.AttributelessFactory;
import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.builder.FactoryContext;
import io.github.factoryfx.factory.builder.FactoryTemplateId;
import io.github.factoryfx.factory.builder.FactoryTreeBuilder;
import io.github.factoryfx.factory.builder.Scope;
import io.github.factoryfx.jetty.AllExceptionMapper;
import io.github.factoryfx.jetty.DefaultObjectMapper;
import io.github.factoryfx.jetty.JerseyServletFactory;
import io.github.factoryfx.jetty.ServletAndPathFactory;
import io.github.factoryfx.jetty.Slf4LoggingFeature;
import jakarta.ws.rs.ext.ExceptionMapper;

/**
 * Builder for a jersey REST resource
 *
 * @param <R> Rootfactory
 */
public class ResourceBuilder<R extends FactoryBase<?, R>> {

    private String pathSpec = "/*";
    private Map<String, Object> jerseyProperties = new HashMap<>();

    FactoryBase<ObjectMapper, R> objectMapper = AttributelessFactory.create(DefaultObjectMapper.class);
    FactoryBase<LoggingFeature, R> loggingFeature = AttributelessFactory.create(Slf4LoggingFeature.class);
    FactoryBase<ExceptionMapper<Throwable>, R> exceptionMapper = AttributelessFactory.create(AllExceptionMapper.class);

    private final List<FactoryTemplateId<? extends FactoryBase<?, R>>> resourceFactoryTemplateIds = new ArrayList<>();
    private final List<FactoryTemplateId<? extends FactoryBase<?, R>>> jaxrsComponentsFactoryTemplateIds = new ArrayList<>();

    private final List<Class<?>> resourceLiveObjectClassList = new ArrayList<>();
    private final List<Class<?>> jaxrsComponentLiveObjectClassList = new ArrayList<>();

    private final FactoryTemplateId<ServletAndPathFactory<R>> servletAndPathFactoryTemplateId;
    private final FactoryTemplateId<JerseyServletFactory<R>> jerseyServletFactoryTemplateId;
    private final FactoryTemplateId<FactoryBase<ExceptionMapper<Throwable>, R>> exceptionMapperTemplateId;
    private final FactoryTemplateId<FactoryBase<ObjectMapper, R>> objectMapperTemplateId;
    private final FactoryTemplateId<FactoryBase<LoggingFeature, R>> loggingFeatureTemplateId;

    public ResourceBuilder(FactoryTemplateId<ServletAndPathFactory<R>> factoryTemplateId) {
        this.servletAndPathFactoryTemplateId = factoryTemplateId;

        this.jerseyServletFactoryTemplateId = new FactoryTemplateId<>(servletAndPathFactoryTemplateId.name + "JerseyServlet", JerseyServletFactory.class);
        this.exceptionMapperTemplateId = new FactoryTemplateId<>(servletAndPathFactoryTemplateId.name + "ExceptionMapper", FactoryBase.class);
        this.objectMapperTemplateId = new FactoryTemplateId<>(servletAndPathFactoryTemplateId.name + "ObjectMapper", FactoryBase.class);
        this.loggingFeatureTemplateId = new FactoryTemplateId<>(servletAndPathFactoryTemplateId.name + "LoggingFeature", FactoryBase.class);
    }

    /**
     * set the base pathSpec for resources default is: /*
     *
     * @param pathSpec servlet pathSpec
     * @return builder
     */
    public ResourceBuilder<R> withPathSpec(String pathSpec) {
        this.pathSpec = pathSpec;
        return this;
    }

    /**
     * jersey properties {@link org.glassfish.jersey.server.ResourceConfig#addProperties}
     *
     * @param jerseyProperties properties e.g (ServerProperties.BV_SEND_ERROR_IN_RESPONSE, true)
     * @return builder
     */
    public ResourceBuilder<R> withJerseyProperties(Map<String, Object> jerseyProperties) {
        this.jerseyProperties = jerseyProperties;
        return this;
    }


    /**
     * add resource {@link FactoryTemplateId}, so that the resource factory can be resolved using the FactoryTreeBuilder. resource is a class with jaxrs annotations
     *
     * @param resourceFactoryTemplateId resource {@link FactoryTemplateId}
     * @return builder
     */
    public <F extends FactoryBase<?, R>> ResourceBuilder<R> withResource(FactoryTemplateId<F> resourceFactoryTemplateId) {
        resourceFactoryTemplateIds.add(resourceFactoryTemplateId);
        return this;
    }

    /**
     * add resource Live Object class. resource is a class with jaxrs annotations
     *
     * @param resourceLiveObjectClass Resource class. Must have available default constructor (see {@link AttributelessFactory})
     * @throws IllegalArgumentException if resourceLiveObjectClass is a subclass of {@link FactoryBase}
     * @return builder
     * */
    public ResourceBuilder<R> withResourceLiveObjectClass(Class<?> resourceLiveObjectClass) {
        if (FactoryBase.class.isAssignableFrom(resourceLiveObjectClass)) {
            throw new IllegalArgumentException("Expected Live Object class. For Factory, use ResourceBuilder#withResource()");
        }
        resourceLiveObjectClassList.add(resourceLiveObjectClass);
        return this;
    }

    /**
     * add jaxrs component {@link FactoryTemplateId}, so that the jaxrs component factory can be resolved using the FactoryTreeBuilder. jaxrs component is comparable to resource but is more generalised e.g. MessageBodyWriter
     *
     * @param jaxrsComponentFactoryTemplateId jaxrsComponent FactoryTemplateId
     * @return builder
     */
    public <F extends FactoryBase<?, R>> ResourceBuilder<R> withJaxrsComponent(FactoryTemplateId<F> jaxrsComponentFactoryTemplateId) {
        jaxrsComponentsFactoryTemplateIds.add(jaxrsComponentFactoryTemplateId);
        return this;
    }

    /**
     * add jaxrs component Live Object class. jaxrs component is comparable to resource but is more generalised e.g. MessageBodyWriter
     *
     * @param jaxrsComponentLiveObjectClass jaxrs component class. Must have available default constructor (see {@link AttributelessFactory})
     * @throws IllegalArgumentException if jaxrsComponentLiveObjectClass is a subclass of {@link FactoryBase}
     * @return builder
     * */
    public ResourceBuilder<R> withJaxrsComponentLiveObjectClass(Class<?> jaxrsComponentLiveObjectClass) {
        if (FactoryBase.class.isAssignableFrom(jaxrsComponentLiveObjectClass)) {
            throw new IllegalArgumentException("Expected Live Object Class. For Factory, use ResourceBuilder#withJaxrsComponent()");
        }
        jaxrsComponentLiveObjectClassList.add(jaxrsComponentLiveObjectClass);
        return this;
    }

    /**
     * configure the REST logging default: {@link Slf4LoggingFeature}
     *
     * @param loggingFeature exceptionMapper factory, shortcut {@code withLoggingFeature(AttributelessFactory.create(Slf4LoggingFeature.class))}
     * @return builder
     */
    public ResourceBuilder<R> withLoggingFeature(FactoryBase<LoggingFeature, R> loggingFeature) {
        this.loggingFeature = loggingFeature;
        return this;
    }

    /**
     * set objectMapper used for all resources default: {@link DefaultObjectMapper}
     *
     * @param objectMapper objectMapper factory, shortcut {@code withObjectMapper(AttributelessFactory.create(DefaultObjectMapper.class))}
     * @return builder
     */
    public ResourceBuilder<R> withObjectMapper(FactoryBase<ObjectMapper, R> objectMapper) {
        this.objectMapper = objectMapper;
        return this;
    }

    /**
     * set the exceptionMapper for all resources. (maps exception to http response)
     *
     * @param exceptionMapper exceptionMapper factory, shortcut {@code withExceptionMapper(AttributelessFactory.create(AllExceptionMapper.class))}
     * @return builder
     */
    public ResourceBuilder<R> withExceptionMapper(FactoryBase<ExceptionMapper<Throwable>, R> exceptionMapper) {
        this.exceptionMapper = exceptionMapper;
        return this;
    }

    FactoryTemplateId<ServletAndPathFactory<R>> getServletAndPathFactoryTemplateId() {
        return servletAndPathFactoryTemplateId;
    }

    boolean match(ResourceBuilder<R> resourceBuilder) {
        return this.pathSpec.equals(resourceBuilder.pathSpec);
    }

    private <F extends FactoryBase<?, R>> void addFactory(FactoryTreeBuilder<?, R> builder, FactoryTemplateId<F> templateId, Scope scope, Function<FactoryContext<R>, F> creator) {
        builder.removeFactory(templateId);
        builder.addFactory(templateId, scope, creator);
    }

    void build(FactoryTreeBuilder<?, R> builder) {
        addFactory(builder, servletAndPathFactoryTemplateId, Scope.PROTOTYPE, (ctx) -> {
            ServletAndPathFactory<R> servletAndPathFactory = new ServletAndPathFactory<>();
            servletAndPathFactory.pathSpec.set(pathSpec);
            servletAndPathFactory.servlet.set(ctx.get(jerseyServletFactoryTemplateId));
            return servletAndPathFactory;
        });


        addFactory(builder, jerseyServletFactoryTemplateId, Scope.PROTOTYPE, (ctx) -> {
            JerseyServletFactory<R> jerseyServlet = new JerseyServletFactory<>();
            jerseyServlet.exceptionMapper.set(ctx.get(exceptionMapperTemplateId));
            jerseyServlet.objectMapper.set(ctx.get(objectMapperTemplateId));
            jerseyServlet.restLogging.set(ctx.get(loggingFeatureTemplateId));
            jerseyServlet.jerseyProperties.set(jerseyProperties);


            for (FactoryTemplateId<? extends FactoryBase<?, R>> factoryTemplateId : resourceFactoryTemplateIds) {
                jerseyServlet.resources.add(ctx.get(factoryTemplateId));
            }

            for (Class<?> clazz : resourceLiveObjectClassList) {
                jerseyServlet.resources.add(AttributelessFactory.create(clazz));
            }

            for (FactoryTemplateId<? extends FactoryBase<?, R>> factoryTemplateId : jaxrsComponentsFactoryTemplateIds) {
                jerseyServlet.additionalJaxrsComponents.add(ctx.get(factoryTemplateId));
            }

            for (Class<?> clazz : jaxrsComponentLiveObjectClassList) {
                jerseyServlet.additionalJaxrsComponents.add(AttributelessFactory.create(clazz));
            }

            return jerseyServlet;
        });

        addFactory(builder, exceptionMapperTemplateId, Scope.PROTOTYPE, (ctx) -> exceptionMapper);

        addFactory(builder, objectMapperTemplateId, Scope.PROTOTYPE, (ctx) -> objectMapper);

        addFactory(builder, loggingFeatureTemplateId, Scope.PROTOTYPE, (ctx) -> loggingFeature);

    }
}
