package io.github.factoryfx.docu.systemtree;

import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.types.StringAttribute;
import io.github.factoryfx.jetty.builder.JettyServerRootFactory;

/**
 * the published endpoint of the greeting service: an ordinary jax-rs resource factory whose
 * configuration is set by the control plane during a system deploy
 */
public class GreetingResourceFactory extends SimpleFactoryBase<GreetingResource, JettyServerRootFactory> {
    public final StringAttribute greeting = new StringAttribute().labelText("greeting").nullable();

    @Override
    protected GreetingResource createImpl() {
        return new GreetingResource(greeting.get());
    }
}
