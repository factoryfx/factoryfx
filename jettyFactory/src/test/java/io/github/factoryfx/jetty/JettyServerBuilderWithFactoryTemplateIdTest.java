package io.github.factoryfx.jetty;

import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.types.StringAttribute;
import io.github.factoryfx.factory.builder.FactoryTemplateId;
import io.github.factoryfx.jetty.builder.FactoryTemplateName;
import io.github.factoryfx.jetty.builder.JettyFactoryTreeBuilder;
import io.github.factoryfx.jetty.builder.JettyServerRootFactory;
import io.github.factoryfx.jetty.builder.ResourceBuilder;
import io.github.factoryfx.server.Microservice;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.jetty.server.Server;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class JettyServerBuilderWithFactoryTemplateIdTest {

    @Path("")
    public static class SomeResource {
        public final String stringValue;

        public SomeResource() {
            this.stringValue = "Default constructor";
        }

        public SomeResource(String stringValue) {
            this.stringValue = stringValue;
        }

        @GET
        @Path("/stringValue")
        public String getStringValue() {
            return this.stringValue;
        }
    }

    static class SomeResourceFactory extends SimpleFactoryBase<SomeResource, JettyServerRootFactory> {

        public final StringAttribute stringAttribute = new StringAttribute().defaultValue("Default Attribute Value");

        @Override
        protected SomeResource createImpl() {
            return new SomeResource(stringAttribute.get());
        }
    }
    static class SomeMessageBodyReaderWriterFactory extends SimpleFactoryBase<JettyServerTest.SomeMessageBodyReaderWriter, JettyServerRootFactory> {
        @Override
        protected JettyServerTest.SomeMessageBodyReaderWriter createImpl() {
            return new JettyServerTest.SomeMessageBodyReaderWriter();
        }
    }


    @Test
    void resource_builder_with_resource_live_object_class_with_factory_class_throws_exception() {

        ResourceBuilder<?> resourceBuilder = new ResourceBuilder<>(new FactoryTemplateId<>(ServletAndPathFactory.class));
        assertThrows(IllegalArgumentException.class, () -> resourceBuilder.withResourceLiveObjectClass(SomeResourceFactory.class));

    }

    @Test
    void resource_builder_with_jaxrs_component_live_object_class_with_factory_class_throws_exception() {

        ResourceBuilder<?> resourceBuilder = new ResourceBuilder<>(new FactoryTemplateId<>(ServletAndPathFactory.class));
        assertThrows(IllegalArgumentException.class, () -> resourceBuilder.withJaxrsComponentLiveObjectClass(SomeMessageBodyReaderWriterFactory.class));

    }

    @Test
    void resource_with_factory_template_id_test() {
        int port = PortUtils.findAvailablePort();

        JettyFactoryTreeBuilder builder = new JettyFactoryTreeBuilder((jetty, ctx) -> {
            jetty.withPort(port).withHost("localhost")
                    .withJersey(rb -> rb.withPathSpec("/resource0/*").withResource(new FactoryTemplateId<>(SomeResourceFactory.class)), new FactoryTemplateName("resource0"))
                    .withJersey(rb -> rb.withPathSpec("/resource1/*").withResource(new FactoryTemplateId<>(SomeResourceFactory.class, "resource1")), new FactoryTemplateName("resource1"))
                    .withJersey(rb -> rb.withPathSpec("/resource2/*").withResource(new FactoryTemplateId<>(SomeResourceFactory.class, "resource2")), new FactoryTemplateName("resource2"));
        });


        builder.addSingleton(SomeResourceFactory.class);

        builder.addSingleton(SomeResourceFactory.class, "resource1", ctx -> {
            SomeResourceFactory factory = new SomeResourceFactory();
            factory.stringAttribute.set("Resource 1");
            return factory;
        });
        builder.addSingleton(SomeResourceFactory.class, "resource2", ctx -> {
            SomeResourceFactory factory = new SomeResourceFactory();
            factory.stringAttribute.set("Resource 2");
            return factory;
        });

        Microservice<Server, JettyServerRootFactory> microservice = builder.microservice().build();


        try (TestClient client = new TestClient("localhost", port)) {
            microservice.start();

            assertEquals("Default Attribute Value", client.get("resource0/stringValue", String.class));
            assertEquals("Resource 1", client.get("resource1/stringValue", String.class));
            assertEquals("Resource 2", client.get("resource2/stringValue", String.class));
        }


    }

    @Test
    void resource_with_live_object_class_test() {
        int port = PortUtils.findAvailablePort();


        JettyFactoryTreeBuilder builder = new JettyFactoryTreeBuilder((jetty, ctx) -> {
            jetty.withPort(port).withHost("localhost")
                    .withJersey(rb -> rb.withPathSpec("/resource1/*").withResourceLiveObjectClass(SomeResource.class), new FactoryTemplateName("resource1"));
        });

        Microservice<Server, JettyServerRootFactory> microservice = builder.microservice().build();


        try (TestClient client = new TestClient("localhost", port)) {
            microservice.start();

            assertEquals("Default constructor", client.get("resource1/stringValue", String.class));
        }

    }

    @Test
    void jaxrs_component_with_factory_template_id_test() {
        int port = PortUtils.findAvailablePort();

        JettyFactoryTreeBuilder builder = new JettyFactoryTreeBuilder((jetty, ctx) -> {
            jetty.withPort(port).withHost("localhost")
                    .withJersey(rb -> rb.withPathSpec("/*")
                            .withResource(new FactoryTemplateId<>(JettyServerTest.MessageBodyReaderWriterEchoFactory.class))
                            .withJaxrsComponentLiveObjectClass(JettyServerTest.SomeMessageBodyReaderWriter.class), new FactoryTemplateName("echo"));
        });

        builder.addSingleton(JettyServerTest.MessageBodyReaderWriterEchoFactory.class);
        builder.addSingleton(SomeMessageBodyReaderWriterFactory.class);

        Microservice<Server, JettyServerRootFactory> microservice = builder.microservice().build();


        try (TestClient client = new TestClient("localhost", port)) {
            microservice.start();

            assertEquals("Changed by writer: Changed by reader: Hello", client.post("echo", Entity.entity("Hello", MediaType.valueOf("my/mime")), String.class));
        }


    }

}
