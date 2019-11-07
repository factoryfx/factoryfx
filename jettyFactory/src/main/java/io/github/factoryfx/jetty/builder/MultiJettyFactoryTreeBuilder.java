package io.github.factoryfx.jetty.builder;

import io.github.factoryfx.factory.builder.FactoryContext;
import io.github.factoryfx.factory.builder.FactoryTemplateId;
import io.github.factoryfx.factory.builder.FactoryTreeBuilder;
import io.github.factoryfx.factory.builder.Scope;
import io.github.factoryfx.jetty.JettyServerFactory;
import org.eclipse.jetty.server.Server;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;

public class MultiJettyFactoryTreeBuilder extends FactoryTreeBuilder<List<Server>, MultiJettyServerRootFactory> {

    public MultiJettyFactoryTreeBuilder() {
        super(new FactoryTemplateId<>(MultiJettyServerRootFactory.class,null),true);

        this.addFactory(this.rootTemplateId, Scope.SINGLETON, ctx->{
            MultiJettyServerRootFactory serverRootFactory = new MultiJettyServerRootFactory();
            for (String builderName : builderNames) {
                serverRootFactory.servers.add(ctx.get(builderName));
            }
            return serverRootFactory;
        });
    }

    private final Set<String> builderNames= new HashSet<>();

    /**
     * .addJetty((jetty,ctx)->jetty
     *                     .withHost("localhost").withPort(8005).withDefaultJersey(rb->rb
     *                     .withResource(ctx.get(ResourceFactory.class))
     *                 );
     *
     * @param jetty setup
     * @return builder
     */
    public MultiJettyFactoryTreeBuilder addJetty(String name, BiConsumer<JettyServerBuilder<List<Server>, MultiJettyServerRootFactory,JettyServerFactory<MultiJettyServerRootFactory>>, FactoryContext<MultiJettyServerRootFactory>> jetty){
        this.addBuilder((ctx)->{
            JettyServerBuilder<List<Server>, MultiJettyServerRootFactory,JettyServerFactory<MultiJettyServerRootFactory>> jettyBuilder = new JettyServerBuilder<>(new FactoryTemplateId<MultiJettyServerRootFactory,JettyServerFactory<MultiJettyServerRootFactory>>(name,JettyServerFactory.class), JettyServerFactory::new);
            jetty.accept(jettyBuilder,ctx);
            return jettyBuilder;
        });
        if (!builderNames.add(name)){
            throw new IllegalStateException("jetty name must be unique: "+name);
        }
        return this;
    }
}
