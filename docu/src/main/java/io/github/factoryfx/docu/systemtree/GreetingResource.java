package io.github.factoryfx.docu.systemtree;

/** jax-rs annotations are inherited from the GreetingApi interface */
public class GreetingResource implements GreetingApi {

    private final String greeting;

    public GreetingResource(String greeting) {
        this.greeting = greeting;
    }

    @Override
    public String greeting() {
        return greeting;
    }
}
