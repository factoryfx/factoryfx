package io.github.factoryfx.microservice.test.systemtree;

import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.types.StringAttribute;

/**
 * spike stand-in for a generic EndpointFactory&lt;Api&gt;: an ordinary jax-rs resource factory whose
 * configuration (here: the served greeting) is set by the control plane during deploy
 */
public class TimeResourceFactory extends SimpleFactoryBase<TimeResource, ARoot> {
    public final StringAttribute greeting = new StringAttribute().labelText("greeting").nullable();

    @Override
    protected TimeResource createImpl() {
        return new TimeResource(greeting.get());
    }
}
