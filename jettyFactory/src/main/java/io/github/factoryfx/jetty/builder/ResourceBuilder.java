package io.github.factoryfx.jetty.builder;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.factoryfx.factory.AttributelessFactory;
import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.builder.FactoryTemplateId;
import io.github.factoryfx.factory.builder.FactoryTreeBuilder;
import io.github.factoryfx.factory.builder.Scope;
import io.github.factoryfx.jetty.*;
import org.glassfish.jersey.logging.LoggingFeature;

import javax.ws.rs.ext.ExceptionMapper;
import java.util.ArrayList;
import java.util.List;

/**
 * Builder for a jersey REST resource
 * @param <R> Rootfactory
 */
public class ResourceBuilder<R extends FactoryBase<?,R>> {

    private String pathSpec="/*";

    FactoryBase<ObjectMapper,R> objectMapper= AttributelessFactory.create(DefaultObjectMapper.class);
    FactoryBase<LoggingFeature,R> loggingFeature = AttributelessFactory.create(Slf4LoggingFeature.class);
    FactoryBase<ExceptionMapper<Throwable>,R> exceptionMapper = AttributelessFactory.create(AllExceptionMapper.class);

    List<FactoryBase<?,R>> resources= new ArrayList<>();
    List<FactoryBase<?,R>> jaxrsComponents= new ArrayList<>();

    private FactoryTemplateId<R,ServletAndPathFactory<R>> servletAndPathFactoryTemplateId;
    private FactoryTemplateId<R,JerseyServletFactory<R>> jerseyServletFactoryTemplateId;
    private FactoryTemplateId<R,FactoryBase<ExceptionMapper<Throwable>,R>> exceptionMapperTemplateId;
    private FactoryTemplateId<R,FactoryBase<ObjectMapper,R>> objectMapperTemplateId;
    private FactoryTemplateId<R,FactoryBase<LoggingFeature,R>> loggingFeatureTemplateId;

    public ResourceBuilder(FactoryTemplateId<R,ServletAndPathFactory<R>> factoryTemplateId) {
        this.servletAndPathFactoryTemplateId = factoryTemplateId;

        this.jerseyServletFactoryTemplateId = new FactoryTemplateId<>(servletAndPathFactoryTemplateId.name + "JerseyServlet", JerseyServletFactory.class);
        this.exceptionMapperTemplateId = new FactoryTemplateId<>(servletAndPathFactoryTemplateId.name + "ExceptionMapper", FactoryBase.class);
        this.objectMapperTemplateId = new FactoryTemplateId<>(servletAndPathFactoryTemplateId.name + "ObjectMapper", FactoryBase.class);
        this.loggingFeatureTemplateId = new FactoryTemplateId<>(servletAndPathFactoryTemplateId.name + "LoggingFeature", FactoryBase.class);
    }

    /**
     * set the base pathSpec for resources default is: /*
     *
     * @return builder
     */
    public ResourceBuilder<R> withPathSpec(String pathSpec){
        this.pathSpec=pathSpec;
        return this;
    }

    /**
     * add resource factory, resource is a class with jaxrs annotations
      @param resource resource factory
     * @return builder
     */
    public ResourceBuilder<R> withResource(FactoryBase<?,R> resource){
        resources.add(resource);
        return this;
    }

    /**
     * jaxrs component is comparable to resource but is more generalised e.g. MessageBodyWriter
     * @param jaxrsComponent jaxrsComponent
     * @return builder
     */
    public ResourceBuilder<R> withJaxrsComponent(FactoryBase<?,R> jaxrsComponent){
        jaxrsComponents.add(jaxrsComponent);
        return this;
    }

    /**
     * configure the REST logging default: {@link Slf4LoggingFeature}
     * @param loggingFeature exceptionMapper factory, shortcut <pre>withLoggingFeature(AttributelessFactory.create(Slf4LoggingFeature.class))</pre>
     * @return builder
     */
    public ResourceBuilder<R> withLoggingFeature(FactoryBase<LoggingFeature,R> loggingFeature){
        this.loggingFeature =loggingFeature;
        return this;
    }

    /**
     * set objectMapper used for all resources default: {@link DefaultObjectMapper}
     * @param objectMapper objectMapper factory, shortcut <pre>withObjectMapper(AttributelessFactory.create(DefaultObjectMapper.class))</pre>
     * @return builder
     */
    public ResourceBuilder<R> withObjectMapper(FactoryBase<ObjectMapper,R> objectMapper){
        this.objectMapper=objectMapper;
        return this;
    }

    /**
     * set the exceptionMapper for all resources. (maps exception to http response)
     * @param exceptionMapper exceptionMapper factory, shortcut <pre>withExceptionMapper(AttributelessFactory.create(AllExceptionMapper.class))</pre>
     * @return builder
     */
    public ResourceBuilder<R> withExceptionMapper(FactoryBase<ExceptionMapper<Throwable>,R> exceptionMapper) {
        this.exceptionMapper =exceptionMapper;
        return this;
    }

    FactoryTemplateId<R,ServletAndPathFactory<R>> getServletAndPathFactoryTemplateId(){
        return servletAndPathFactoryTemplateId;
    }

    boolean match(ResourceBuilder<R> resourceBuilder) {
        return this.pathSpec.equals(resourceBuilder.pathSpec);
    }

    void build(FactoryTreeBuilder<?, R> builder) {
        builder.addFactory(servletAndPathFactoryTemplateId, Scope.PROTOTYPE, (ctx) -> {
            ServletAndPathFactory<R> servletAndPathFactory = new ServletAndPathFactory<>();
            servletAndPathFactory.pathSpec.set(pathSpec);
            servletAndPathFactory.servlet.set(ctx.get(jerseyServletFactoryTemplateId));
            return servletAndPathFactory;
        });


        builder.addFactory(jerseyServletFactoryTemplateId, Scope.PROTOTYPE, (ctx) -> {
            JerseyServletFactory<R> jerseyServlet = new JerseyServletFactory<>();
            jerseyServlet.exceptionMapper.set(ctx.get(exceptionMapperTemplateId));
            jerseyServlet.objectMapper.set(ctx.get(objectMapperTemplateId));
            jerseyServlet.restLogging.set(ctx.get(loggingFeatureTemplateId));

            jerseyServlet.resources.addAll(resources);

            jerseyServlet.additionalJaxrsComponents.addAll(jaxrsComponents);
            return jerseyServlet;
        });

        builder.addFactory(exceptionMapperTemplateId, Scope.PROTOTYPE, (ctx) -> {
            return exceptionMapper;
        });

        builder.addFactory(objectMapperTemplateId, Scope.PROTOTYPE, (ctx) -> {
            return objectMapper;
        });

        builder.addFactory(loggingFeatureTemplateId, Scope.PROTOTYPE, (ctx) -> {
            return loggingFeature;
        });
    }
}
