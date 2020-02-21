package io.github.factoryfx.jetty;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.function.Supplier;

import org.eclipse.jetty.server.Server;
import org.junit.jupiter.api.Test;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;
import io.github.factoryfx.factory.builder.FactoryTemplateId;
import io.github.factoryfx.factory.builder.FactoryTreeBuilder;
import io.github.factoryfx.jetty.builder.JettyServerBuilder;
import io.github.factoryfx.server.Microservice;

public class PersistentSingletonTest {

    public static class RootFactory extends SimpleFactoryBase<Void, RootFactory> {
        public final FactoryAttribute<Server, JettyServerFactory<RootFactory>> jettyServer = new FactoryAttribute<>();
        public final FactoryAttribute<Object, Fact1<RootFactory>> fact1 = new FactoryAttribute<>();

        @Override
        protected Void createImpl() {
            return null;
        }
    }

    public static class Fact1<R extends FactoryBase<?, R>> extends SimpleFactoryBase<Object, R> {
        @Override
        protected Object createImpl() { return new Object(); }
    }

    //@Test
    void brokenSingleton() {

        FactoryTreeBuilder<Void, RootFactory> factoryTreeBuilder = new FactoryTreeBuilder<>(RootFactory.class);
        factoryTreeBuilder.addBuilder(ctx -> new JettyServerBuilder<>(new FactoryTemplateId<>(null, JettyServerFactory.class),
                                                                      (Supplier<JettyServerFactory<RootFactory>>) JettyServerFactory::new)
            .withResource(ctx.get(Fact1.class)));

        factoryTreeBuilder.addSingleton(Fact1.class);
        Microservice<Void, RootFactory> microservice = factoryTreeBuilder.microservice().build();
        microservice.start();

        RootFactory rootFactoryFact1 = microservice.prepareNewFactory().root;
        assertEquals(rootFactoryFact1.getId(),
                     ((Fact1) ((JerseyServletFactory)(((ServletAndPathFactory) ((UpdateableServletFactory) ((ServletContextHandlerFactory) ((GzipHandlerFactory)
                         rootFactoryFact1.jettyServer.get().handler.get().handlers.get().get(0))
                         .handler.get()).updatableRootServlet.get())
                         .servletAndPaths.get().get(0))
                         .servlet.get())).resources.get(0)).getId());

    }

    public static class RootFactory2 extends SimpleFactoryBase<Void, RootFactory2> {
        public final FactoryAttribute<Object, Fact1<RootFactory2>> fact1 = new FactoryAttribute<>();
        public final FactoryAttribute<Server, JettyServerFactory<RootFactory2>> jettyServer = new FactoryAttribute<>();

        @Override
        protected Void createImpl() {
            return null;
        }
    }

    @Test
    void nowItWorks() {

        FactoryTreeBuilder<Void, RootFactory2> factoryTreeBuilder = new FactoryTreeBuilder<>(RootFactory2.class);
        factoryTreeBuilder.addBuilder(ctx -> new JettyServerBuilder<>(new FactoryTemplateId<>(null, JettyServerFactory.class),
                                                                      (Supplier<JettyServerFactory<RootFactory2>>) JettyServerFactory::new)
            .withResource(ctx.get(Fact1.class)));

        factoryTreeBuilder.addSingleton(Fact1.class);
        Microservice<Void, RootFactory2> microservice = factoryTreeBuilder.microservice().build();
        microservice.start();

        RootFactory2 root = microservice.prepareNewFactory().root;
        assertEquals(root.fact1.get().getId(),
                     ((Fact1) ((JerseyServletFactory)(((ServletAndPathFactory) ((UpdateableServletFactory) ((ServletContextHandlerFactory) ((GzipHandlerFactory) root.jettyServer.get().handler.get().handlers.get()
                                                                                                                                                                                                                                               .get(0)).handler
                         .get()).updatableRootServlet.get()).servletAndPaths.get().get(0)).servlet.get())).resources.get(0)).getId());
    }

    @Test
    void alsoLikeThis() {

        FactoryTreeBuilder<Void, RootFactory> factoryTreeBuilder = new FactoryTreeBuilder<>(RootFactory.class);
        factoryTreeBuilder.addBuilder(ctx -> new JettyServerBuilder<>(new FactoryTemplateId<>(null, JettyServerFactory.class),
                                                                      (Supplier<JettyServerFactory<RootFactory>>) JettyServerFactory::new)
            .withResource(ctx.get(Fact1.class)));

        factoryTreeBuilder.addSingleton(Fact1.class);
        factoryTreeBuilder.markAsNonPersistentFactoryBuilder();
        Microservice<Void, RootFactory> microservice = factoryTreeBuilder.microservice().build();
        microservice.start();

        RootFactory root = microservice.prepareNewFactory().root;
        assertEquals(root.fact1.get().getId(),
                     ((Fact1) ((JerseyServletFactory)(((ServletAndPathFactory) ((UpdateableServletFactory) ((ServletContextHandlerFactory) ((GzipHandlerFactory) root.jettyServer.get().handler.get().handlers.get()
                                                                                                                                                                                                                                               .get(0)).handler
                         .get()).updatableRootServlet.get()).servletAndPaths.get().get(0)).servlet.get())).resources.get(0)).getId());
    }
}
