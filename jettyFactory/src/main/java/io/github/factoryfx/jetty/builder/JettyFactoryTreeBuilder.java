package io.github.factoryfx.jetty.builder;

import io.github.factoryfx.factory.builder.FactoryContext;
import io.github.factoryfx.factory.builder.FactoryTemplateId;
import io.github.factoryfx.factory.builder.FactoryTreeBuilder;
import org.eclipse.jetty.server.Server;

import java.util.function.BiConsumer;

/**
 * special variation if the FactoryTreeBuilder with easier jetty server setup
 */
public class JettyFactoryTreeBuilder extends FactoryTreeBuilder<Server, JettyServerRootFactory> {

    /**
     * new JettyFactoryTreeBuilder((jetty,ctx)->jetty
     *                     .withHost("localhost").withPort(8005).withDefaultJersey(rb->rb
     *                     .withResource(ctx.get(ResourceFactory.class))
     *                 );
     * @param jetty jetty builder setup function
     */
    public JettyFactoryTreeBuilder(BiConsumer<JettyServerBuilder<JettyServerRootFactory,JettyServerRootFactory>, FactoryContext<JettyServerRootFactory>> jetty) {
        this(new FactoryTemplateId<>(JettyServerRootFactory.class, "DefaultJettySetup"),jetty);
    }

    private JettyFactoryTreeBuilder(FactoryTemplateId<JettyServerRootFactory> rootTemplateId, BiConsumer<JettyServerBuilder<JettyServerRootFactory,JettyServerRootFactory>, FactoryContext<JettyServerRootFactory>> jetty) {
        super(rootTemplateId,builder ->{
            builder.addBuilder((ctx)->{
                JettyServerBuilder<JettyServerRootFactory,JettyServerRootFactory> jettyBuilder = new JettyServerBuilder<>(rootTemplateId, JettyServerRootFactory::new);
                jetty.accept(jettyBuilder,ctx);
                return jettyBuilder;
            });
        });
    }
}
