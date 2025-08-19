package io.github.factoryfx.jetty.builder;

import io.github.factoryfx.factory.AttributelessFactory;
import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;
import io.github.factoryfx.factory.builder.FactoryTemplateId;
import io.github.factoryfx.factory.builder.FactoryTreeBuilder;
import io.github.factoryfx.factory.storage.DataUpdate;
import io.github.factoryfx.jetty.*;
import io.github.factoryfx.server.Microservice;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import org.eclipse.jetty.server.Server;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReplaceJettyServerFactoryTest {

    static class Root {
    }

    static class RootFactory extends SimpleFactoryBase<Root, RootFactory> {

        public final FactoryAttribute<Server, JettyServerFactory<RootFactory>> server = new FactoryAttribute<>();

        @Override
        protected Root createImpl() {
            return new Root();
        }
    }

    static class MyServletFactory extends JerseyServletFactory<RootFactory> {
        public final FactoryAttribute<MyResource, MyResourceFactory> myResource = new FactoryAttribute<>();

        protected List<Object> getResourcesInstances() {
            return List.of(myResource.instance());
        }

    }

    @Path("")
    static class MyResource {
        @GET
        @Path("test")
        public Response test() {
            return Response.ok().build();
        }
    }

    static class MyResourceFactory extends SimpleFactoryBase<MyResource, RootFactory> {
        @Override
        protected MyResource createImpl() {
            return new MyResource();
        }
    }


    private static final FactoryTemplateId<JettyServerFactory<RootFactory>> oldServerBuilderFactoryTemplateId = new FactoryTemplateId<>("oldServer", JettyServerFactory.class);
    private static final FactoryTemplateId<JettyServerFactory<RootFactory>> newServerBuilderFactoryTemplateId = new FactoryTemplateId<>("newServer", JettyServerFactory.class);

    enum ServerBuilder {
        OLD_BUILDER,
        NEW_BUILDER,
    }


    Microservice<Root, RootFactory> createMicroservice(ServerBuilder serverBuilder) {

        FactoryTreeBuilder<Root, RootFactory> builder = new FactoryTreeBuilder<>(RootFactory.class, ctx -> {
            RootFactory rootFactory = new RootFactory();
            rootFactory.server.set(ctx.get(serverBuilder == ServerBuilder.OLD_BUILDER ? oldServerBuilderFactoryTemplateId : newServerBuilderFactoryTemplateId));
            return rootFactory;
        });


        builder.addSingleton(MyServletFactory.class, ctx -> {
            MyServletFactory factory = new MyServletFactory();
            factory.objectMapper.set(AttributelessFactory.create(DefaultObjectMapper.class));
            factory.restLogging.set(AttributelessFactory.create(Slf4LoggingFeature.class));
            factory.exceptionMapper.set(AttributelessFactory.create(AllExceptionMapper.class));
            factory.myResource.set(ctx.get(MyResourceFactory.class));
            return factory;
        });

        if (serverBuilder == ServerBuilder.OLD_BUILDER) {

            builder.addBuilder(ctx ->
                    new JettyServerBuilder<>(oldServerBuilderFactoryTemplateId,
                            JettyServerFactory::new)
                            .withServlet(ctx.get(MyServletFactory.class), "/myresource/*", new FactoryTemplateName("myServlet"))
            );

        }  else if (serverBuilder == ServerBuilder.NEW_BUILDER) {

            // This builder passes the FactoryTemplateId of the resource, so that it can be instantiated later, when the FactoryContext is aware of the stored factory instance
            builder.addBuilder(ctx ->
                    new JettyServerBuilder<>(newServerBuilderFactoryTemplateId,
                            JettyServerFactory::new)
                            .withJersey(rb -> rb.withResource(new FactoryTemplateId<>(MyResourceFactory.class)).withPathSpec("/myresource/*"), new FactoryTemplateName("myresource"))
            );

        }

        builder.addSingleton(MyResourceFactory.class);


        return builder.microservice().withFilesystemStorage(folder)
                .build();
    }

    MyResourceFactory findMyResource(JettyServerFactory<RootFactory> serverFactory) {
        return ((JerseyServletFactory<RootFactory>) ((ServletContextHandlerFactory<RootFactory>) serverFactory.handler.get().handlers.get(GzipHandlerFactory.class).handler.get()).updatableRootServlet.get().servletAndPaths.get(0).servlet.get()).resources.get(MyResourceFactory.class);
    }


    @TempDir
    static java.nio.file.Path folder;

    @BeforeEach
    void setupAll() {
        System.out.printf("Before each: %s%n", folder.toAbsolutePath());

        // Prepare the persisted configuration, with the old server builder
        Microservice<Root, RootFactory> microservice = createMicroservice(ServerBuilder.OLD_BUILDER);

        microservice.start();
        microservice.stop();
    }


    @Test
    void new_with_factory_template_id_test() {
        System.out.printf("with_factory_template_id_test: %s%n", folder.toAbsolutePath());

        Microservice<Root, RootFactory> microservice = createMicroservice(ServerBuilder.NEW_BUILDER);

        microservice.start();

        DataUpdate<RootFactory> update = microservice.prepareNewFactory();
        FactoryTreeBuilder<Root, RootFactory> builder = (FactoryTreeBuilder<Root, RootFactory>) update.root.utility().getFactoryTreeBuilder();

        List<MyResourceFactory> list = update.root.internal().collectChildrenDeep().stream().filter(f -> f instanceof MyResourceFactory).map(f -> (MyResourceFactory) f).toList();
        assertEquals(1, list.size());

        UUID originalMyResourceFactoryId = list.get(0).getId();

        System.out.printf("originalMyResourceFactoryId=%s%n", originalMyResourceFactoryId);
        JettyServerFactory<RootFactory> newServer = builder.buildSubTree(newServerBuilderFactoryTemplateId);

        MyResourceFactory newMyResourceFactory = findMyResource(newServer);
        assertEquals(originalMyResourceFactoryId, newMyResourceFactory.getId(), "Fix: the new MyResourceFactory instance respects the Scope.SIMPLETON scope");

    }
}
