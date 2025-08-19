package io.github.factoryfx.example.server.factoryupdatelogleak;

import io.github.factoryfx.factory.builder.FactoryTemplateId;
import io.github.factoryfx.jetty.builder.FactoryTemplateName;
import io.github.factoryfx.jetty.builder.JettyFactoryTreeBuilder;
import io.github.factoryfx.server.Microservice;

/**
 * This example showcases the correct way to set up your Jersey servlets and resources to avoid leaking FactoryUpdateLog when triggering a configuration update with for example {@link io.github.factoryfx.microservice.rest.MicroserviceResource}
 * <p>
 * Simply said, any resource that calls {@link Microservice#updateCurrentFactory} MUST NOT be on the same servlet as any other resource that has a Factory dependency that would be updated
 * <p>
 * In this sample application, we have
 * <p>
 * {@link SimplifiedMicroserviceResource}: it triggers {@link Microservice#updateCurrentFactory} on a boolean attribute to which {@link SomeOtherResourceFactory} depends on through {@link NestedObjectFactory}
 * <p>
 * In {@link FactoryUpdateLogLeak#leakySetup()}, both resources are under the same servlet -> leak
 * In {@link FactoryUpdateLogLeak#correctSetup()}, each resource has their own servlet -> no leak
 */
public class FactoryUpdateLogLeak {

    private static final int PORT = 8240;

    public static void main(String[] args) {

        JettyFactoryTreeBuilder builder;

//        builder = leakySetup();
        builder = correctSetup();


        builder.microservice().build().start();


    }

    /**
     * In this setup, both the {@link SimplifiedMicroserviceResource} and {@link SomeOtherResource} are under the same servlet
     * <p>
     * Since SomeOtherResourceFactory depends on {@link NestedObjectFactory#booleanAttribute}, triggering the update through {@link SimplifiedMicroserviceResource}
     * will trigger the update of {@link io.github.factoryfx.jetty.JerseyServletFactory}, which will execute while the "updateTrigger" request is still flying, and the Jetty context will be leaked to the ThreadLocal
     * <p>
     * Reproduce
     * 1. Start with leakySetup
     * 2. With an HTTP client, send a GET request to localhost:<PORT>/other/nested/boolean (i.e `curl localhost:<PORT>/other/nested/boolean`): response should be false
     * 3. With an HTTP client, send a GET request to localhost:<PORT>/microservice/triggerUpdate (i.e `curl localhost:<PORT>/microservice/triggerUpdate`)
     * 4. With an HTTP client, send a GET request to localhost:<PORT>/other/nested/boolean (i.e `curl localhost:<PORT>/other/nested/boolean`): response should be true
     * 5. From the IntelliJ 'Run' section, 'Profile the process' > 'Capture memory snapshot'. In the result, search for "FactoryUpdateLog" -> there will be 1 instance retained (so, leaked)
     */
    private static JettyFactoryTreeBuilder leakySetup() {
        JettyFactoryTreeBuilder builder = new JettyFactoryTreeBuilder((jetty, ctx) -> {
            jetty.withResource(new FactoryTemplateId<>(SimplifiedMicroserviceResourceFactory.class))
                    .withResource(new FactoryTemplateId<>(SomeOtherResourceFactory.class))
                    .withHost("localhost")
                    .withPort(PORT);
        });

        builder.addSingleton(SimplifiedMicroserviceResourceFactory.class);
        builder.addSingleton(SomeOtherResourceFactory.class);

        builder.addSingleton(NestedObjectFactory.class);
        return builder;
    }

    /**
     * In this setup, the {@link SimplifiedMicroserviceResource} and {@link SomeOtherResource} each have their own servlet. On triggerUpdate, only the servlet2 will be updated, and so it will not leak the triggerUpdate jetty context
     * <p>
     * Reproduce
     * 1. Start with correctSetup
     * 2. With an HTTP client, send a GET request to localhost:<PORT>/servlet2/other/nested/boolean (i.e `curl localhost:<PORT>/other/nested/boolean`): response should be false
     * 3. With an HTTP client, send a GET request to localhost:<PORT>/servlet1/microservice/triggerUpdate (i.e `curl localhost:<PORT>/microservice/triggerUpdate`)
     * 4. With an HTTP client, send a GET request to localhost:<PORT>/servlet2/other/nested/boolean (i.e `curl localhost:<PORT>/other/nested/boolean`): response should be true
     * 5. From the IntelliJ 'Run' section, 'Profile the process' > 'Capture memory snapshot'. In the result, search for "FactoryUpdateLog" -> there will be 0 instance retained (so, didn't leak)
     */
    private static JettyFactoryTreeBuilder correctSetup() {
        JettyFactoryTreeBuilder builder = new JettyFactoryTreeBuilder((jetty, ctx) -> {
            jetty.withJersey(rb -> rb.withResource(new FactoryTemplateId<>(SimplifiedMicroserviceResourceFactory.class)).withPathSpec("/servlet1/*"), new FactoryTemplateName("servlet1"))
                    .withJersey(rb -> rb.withResource(new FactoryTemplateId<>(SomeOtherResourceFactory.class)).withPathSpec("/servlet2/*"), new FactoryTemplateName("servlet2"))
                    .withResource(new FactoryTemplateId<>(SomeOtherResourceFactory.class))
                    .withHost("localhost")
                    .withPort(PORT);
        });

        builder.addSingleton(SimplifiedMicroserviceResourceFactory.class);
        builder.addSingleton(SomeOtherResourceFactory.class);

        builder.addSingleton(NestedObjectFactory.class);
        return builder;
    }
}
