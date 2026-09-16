package io.github.factoryfx.microservice.test.systemtree;

/** jax-rs annotations are inherited from the TimeApi interface (same pattern as MicroserviceResource) */
public class TimeResource implements TimeApi {

    private final String greeting;

    public TimeResource(String greeting) {
        this.greeting = greeting;
    }

    @Override
    public String greeting() {
        return greeting;
    }
}
