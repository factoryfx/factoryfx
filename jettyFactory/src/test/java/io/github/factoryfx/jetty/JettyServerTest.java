package io.github.factoryfx.jetty;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.google.common.io.ByteStreams;
import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.types.EnumAttribute;
import io.github.factoryfx.factory.builder.FactoryTemplateId;
import io.github.factoryfx.factory.builder.Scope;
import io.github.factoryfx.factory.merge.DataMerger;
import io.github.factoryfx.factory.merge.MergeDiffInfo;
import io.github.factoryfx.factory.storage.DataUpdate;
import io.github.factoryfx.jetty.builder.FactoryTemplateName;
import io.github.factoryfx.jetty.builder.JettyFactoryTreeBuilder;
import io.github.factoryfx.jetty.builder.JettyServerRootFactory;
import io.github.factoryfx.server.Microservice;
import jakarta.servlet.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.*;
import jakarta.ws.rs.ext.MessageBodyReader;
import jakarta.ws.rs.ext.MessageBodyWriter;
import org.eclipse.jetty.server.Server;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class JettyServerTest {
    @Path("/Resource1")
    public static class Resource1{
        @GET()
        public Response get(){
            return Response.ok().build();
        }
    }

    public static class Resource1Factory extends SimpleFactoryBase<Resource1, JettyServerRootFactory> {
        @Override
        protected Resource1 createImpl() {
            return new Resource1();
        }
    }

    @BeforeAll
    public static void setup(){
        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.INFO);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void test_change_port() {
        JettyFactoryTreeBuilder builder = new JettyFactoryTreeBuilder((jetty, ctx)-> jetty.
                withHost("localhost").withPort(8087).withResource(new FactoryTemplateId<>(Resource1Factory.class))
        );

        builder.addFactory(Resource1Factory.class, Scope.SINGLETON, ctx -> {
            return new Resource1Factory();
        });
        Microservice<Server, JettyServerRootFactory> microservice = builder.microservice().build();
        try {
            microservice.start();

            try {
                HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();
                HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8087/Resource1")).GET().build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                Assertions.assertEquals(200, response.statusCode());
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }

            DataUpdate<JettyServerRootFactory> update = microservice.prepareNewFactory();
            update.root.connectors.get(0).port.set(8082);
            microservice.updateCurrentFactory(update);


            try {
                HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();
                HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8087/Resource1")).GET().build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                Assertions.fail("expect ConnectException");
            } catch (IOException | InterruptedException e) {
                //expected
            }

            try {
                HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();
                HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8082/Resource1")).GET().build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                Assertions.assertEquals(200, response.statusCode());
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        } finally {
            microservice.stop();
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void test_remove_connector() {
        JettyFactoryTreeBuilder builder = new JettyFactoryTreeBuilder((jetty, ctx)->{
            jetty.withHost("localhost").withPort(8087).withResource(new FactoryTemplateId<>(Resource1Factory.class)
            );
        });


        builder.addFactory(Resource1Factory.class, Scope.SINGLETON, ctx -> {
            return new Resource1Factory();
        });
        Microservice<Server, JettyServerRootFactory> microservice = builder.microservice().build();
        try{
            microservice.start();

            try {
                HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();
                HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8087/Resource1")).GET().build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                Assertions.assertEquals(200,response.statusCode());
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }

            DataUpdate<JettyServerRootFactory> update = microservice.prepareNewFactory();
            update.root.connectors.clear();
            microservice.updateCurrentFactory(update);


            try {
                HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();
                HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8087/Resource1")).GET().build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                Assertions.fail("expect ConnectException");
            } catch (IOException | InterruptedException e) {
                //expected
            }

        } finally {
            microservice.stop();
        }
    }

    @Path("/Resource")
    public static class LateResponseTestResource {

        public LateResponseTestResource() {

        }

        @GET()
        public Response get() throws InterruptedException {
            Thread.sleep(400);
            return Response.ok("RESPONSE").build();
        }
    }

    public static class LateResponseTestResourceFactory extends SimpleFactoryBase<LateResponseTestResource, JettyServerRootFactory> {
        @Override
        protected LateResponseTestResource createImpl() {
            return new LateResponseTestResource();
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void test_lateResponse() throws InterruptedException, ExecutionException, TimeoutException {
        JettyFactoryTreeBuilder builder = new JettyFactoryTreeBuilder((jetty, ctx)->{
            jetty.withHost("localhost").withPort(8015).withResource(new FactoryTemplateId<>(LateResponseTestResourceFactory.class)
            );
        });
        builder.addFactory(LateResponseTestResourceFactory.class, Scope.SINGLETON, ctx -> {
            return new LateResponseTestResourceFactory();
        });
        Microservice<Server, JettyServerRootFactory> microservice = builder.microservice().build();

        try {
            microservice.start();
            CompletableFuture<String> lateResponse = new CompletableFuture<>();
            HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8015/Resource")).GET().build();
            new Thread() {
                public void run() {
                    try {
                        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                        lateResponse.complete(response.body());
                    } catch (Exception ex) {
                        lateResponse.completeExceptionally(ex);
                    }
                }
            }.start();
            Thread.sleep(200);
            DataUpdate<JettyServerRootFactory> update = microservice.prepareNewFactory();
            update.root.clearResource(LateResponseTestResourceFactory.class);
            microservice.updateCurrentFactory(update);
            try {
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                Assertions.assertEquals(404,response.statusCode());
            } catch (Exception expected) {}
            Assertions.assertEquals("RESPONSE",lateResponse.get(1000, TimeUnit.MILLISECONDS));

        } finally {
            microservice.stop();
        }
    }

    static final class MyMime {
        public String data;
    }


    @Produces("my/mime")
    @Consumes("my/mime")
    public static final class SomeMessageBodyReaderWriter implements MessageBodyWriter<MyMime>, MessageBodyReader<MyMime> {

        @Override
        public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
            return MyMime.class.isAssignableFrom(type);
        }

        @Override
        public MyMime readFrom(Class<MyMime> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ByteStreams.copy(entityStream, out);
            MyMime ret = new MyMime();
            ret.data = "Changed by reader: "+out.toString(StandardCharsets.UTF_8);
            return ret;
        }

        @Override
        public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
            return MyMime.class.isAssignableFrom(type);
        }

        @Override
        public void writeTo(MyMime s, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
            entityStream.write(("Changed by writer: "+s.data).getBytes(StandardCharsets.UTF_8));
        }
    }

    @Path("/echo")
    public static final class MessageBodyReaderWriterEcho {

        @POST
        public MyMime echo(MyMime req) {
            return req;
        }

    }

    public static final class MessageBodyReaderWriterEchoFactory extends SimpleFactoryBase<MessageBodyReaderWriterEcho, JettyServerRootFactory> {
        @Override
        protected MessageBodyReaderWriterEcho createImpl() {
            return new MessageBodyReaderWriterEcho();
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testMessageBodyReader() {
        JettyFactoryTreeBuilder builder = new JettyFactoryTreeBuilder((jetty, ctx)->{
            jetty.withHost("localhost").withPort(8015).withResource(new FactoryTemplateId<>(MessageBodyReaderWriterEchoFactory.class))
                        .withJaxrsComponentLiveObjectClass(SomeMessageBodyReaderWriter.class);
        });

        builder.addFactory(MessageBodyReaderWriterEchoFactory.class, Scope.SINGLETON);

        Microservice<Server, JettyServerRootFactory> microservice = builder.microservice().build();
        try{
            microservice.start();

            ClientConfig cc = new ClientConfig();
            Client client = ClientBuilder.newBuilder().withConfig(cc).build();
            String resp = client.target("http://localhost:8015/echo").request().buildPost(Entity.entity("Hello", MediaType.valueOf("my/mime"))).invoke().readEntity(String.class);

            Assertions.assertEquals("Changed by writer: Changed by reader: Hello", resp);

        } finally {
            microservice.stop();
        }
    }

    @Test
    public void test_custom_jersey() {
        JettyFactoryTreeBuilder builder = new JettyFactoryTreeBuilder((jetty, ctx)->{
            jetty.withHost("localhost").withPort(8087).withJersey(rb->rb.withResource(new FactoryTemplateId<>(Resource1Factory.class)),new FactoryTemplateName("/new/*"));
        });
        builder.addFactory(Resource1Factory.class, Scope.SINGLETON, ctx -> {
            return new Resource1Factory();
        });
    }

    @Test
    public void test_double_build() {
        JettyFactoryTreeBuilder builder = new JettyFactoryTreeBuilder((jetty, ctx)->{
            jetty.withHost("localhost").withPort(8087).withJersey(rb->rb.withResource(new FactoryTemplateId<>(Resource1Factory.class)),new FactoryTemplateName("/new/*"));
        });
        builder.addFactory(Resource1Factory.class, Scope.SINGLETON, ctx -> {
            return new Resource1Factory();
        });

        builder.buildTreeUnvalidated();
        builder.buildTreeUnvalidated();
    }

    @Test
    public void test_rebuild() {
        JettyFactoryTreeBuilder builder = new JettyFactoryTreeBuilder((jetty, ctx)->{
            jetty.withHost("localhost").withPort(8087).withJersey(rb->rb.withResource(new FactoryTemplateId<>(Resource1Factory.class)),new FactoryTemplateName("/new/*"));
        });
        builder.addFactory(Resource1Factory.class, Scope.SINGLETON, ctx -> {
            return new Resource1Factory();
        });
        JettyServerRootFactory root = builder.buildTreeUnvalidated();

        JettyServerRootFactory rebuildRoot = builder.rebuildTreeUnvalidated(root.internal().collectChildrenDeep());


        DataMerger<JettyServerRootFactory> merge = new DataMerger<>(root,root.internal().copy(),rebuildRoot);
        MergeDiffInfo<JettyServerRootFactory> exampleFactoryAMergeDiffInfo = merge.mergeIntoCurrent((p) -> true);
        Assertions.assertEquals(0,exampleFactoryAMergeDiffInfo.mergeInfos.size());
        Assertions.assertEquals(0,exampleFactoryAMergeDiffInfo.conflictInfos.size());

    }

    public static class Slf4LoggingFeatureTestFactory extends SimpleFactoryBase<Slf4LoggingFeature, JettyServerRootFactory>{
        public final EnumAttribute<Slf4LoggingFeatureLogger.JerseyLogLevel> logLevel = new EnumAttribute<>();

        @Override
        protected Slf4LoggingFeature createImpl() {
            return new Slf4LoggingFeature(logLevel.get());
        }
    }

    @Test
    public void test_log_level() {

        ch.qos.logback.classic.Logger logger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Slf4LoggingFeatureLogger.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);



        JettyFactoryTreeBuilder builder = new JettyFactoryTreeBuilder((jetty, ctx)->{
            jetty.withHost("localhost").withPort(8087).withResource(new FactoryTemplateId<>(Resource1Factory.class))
                    .withLoggingFeature(ctx.getUnsafe(Slf4LoggingFeatureTestFactory.class));
        });
        builder.addFactory(Resource1Factory.class, Scope.SINGLETON, ctx -> {
            return new Resource1Factory();
        });
        builder.addSingleton(Slf4LoggingFeatureTestFactory.class, ctx->{
            Slf4LoggingFeatureTestFactory slf4LoggingFeatureTestFactory = new Slf4LoggingFeatureTestFactory();
            slf4LoggingFeatureTestFactory.logLevel.set(Slf4LoggingFeatureLogger.JerseyLogLevel.ERROR);
            return slf4LoggingFeatureTestFactory;
        });

        Microservice<Server, JettyServerRootFactory> microservice = builder.microservice().build();
        try{
            microservice.start();

            ClientConfig cc = new ClientConfig();
            Client client = ClientBuilder.newBuilder().withConfig(cc).build();
            Response response = client.target("http://localhost:8087/Resource1").request().buildGet().invoke();
            Assertions.assertEquals(200, response.getStatus());

        } finally {
            microservice.stop();
        }

        Assertions.assertEquals(Level.ERROR, listAppender.list.get(0).getLevel());
        listAppender.stop();
        logger.detachAppender(listAppender);
    }

    @Test
    public void test_httpConfiguration_sendServerVersion() {
        JettyFactoryTreeBuilder builder = new JettyFactoryTreeBuilder((jetty, ctx)->{
            jetty.withHost("localhost").withPort(8087).withResource(new FactoryTemplateId<>(Resource1Factory.class))
                    .withHttpConfiguration(ctx.getUnsafe(HttpConfigurationFactory.class));
        });
        builder.addFactoryUnsafe(HttpConfigurationFactory.class, Scope.SINGLETON,ctx -> {
            HttpConfigurationFactory<JettyServerRootFactory> httpConfigurationFactory = new HttpConfigurationFactory<>();
            httpConfigurationFactory.sendServerVersion.set(true);
            return httpConfigurationFactory;
        });
        builder.addSingleton(Resource1Factory.class, ctx -> {
            return new Resource1Factory();
        });

        Microservice<Server, JettyServerRootFactory> microservice = builder.microservice().build();
        try{
            microservice.start();

            ClientConfig cc = new ClientConfig();
            Client client = ClientBuilder.newBuilder().withConfig(cc).build();
            Response response = client.target("http://localhost:8087/Resource1").request().buildGet().invoke();
            Assertions.assertTrue(response.getHeaders().get("Server").get(0).toString().contains("Jetty"));
            Assertions.assertEquals(200, response.getStatus());

        } finally {
            microservice.stop();
        }
    }

    @Test
    public void test_httpConfiguration_notServerVersion() {
        JettyFactoryTreeBuilder builder = new JettyFactoryTreeBuilder((jetty, ctx)->{
            jetty.withHost("localhost").withPort(8087).withResource(new FactoryTemplateId<>(Resource1Factory.class))
                    .withHttpConfiguration(ctx.getUnsafe(HttpConfigurationFactory.class));
        });
        builder.addFactoryUnsafe(HttpConfigurationFactory.class, Scope.SINGLETON,ctx -> {
            HttpConfigurationFactory<JettyServerRootFactory> httpConfigurationFactory = new HttpConfigurationFactory<>();
            httpConfigurationFactory.sendServerVersion.set(false);
            return httpConfigurationFactory;
        });
        builder.addSingleton(Resource1Factory.class, ctx -> {
            return new Resource1Factory();
        });

        Microservice<Server, JettyServerRootFactory> microservice = builder.microservice().build();
        try{
            microservice.start();

            ClientConfig cc = new ClientConfig();
            Client client = ClientBuilder.newBuilder().withConfig(cc).build();
            Response response = client.target("http://localhost:8087/Resource1").request().buildGet().invoke();
            Assertions.assertNull(response.getHeaders().get("Server"));
            Assertions.assertEquals(200, response.getStatus());

        } finally {
            microservice.stop();
        }
    }


    @Test
    public void test_servletFilter() {
        JettyFactoryTreeBuilder builder = new JettyFactoryTreeBuilder((jetty, ctx)->{
            jetty.withHost("localhost").withPort(8087).withServletFilter(ctx.get(ServletFilterFactory.class),"/*",new FactoryTemplateName("filter1"));
        });
        builder.addSingleton(ServletFilterFactory.class, ctx -> {
            return new ServletFilterFactory();
        });

        Microservice<Server, JettyServerRootFactory> microservice = builder.microservice().build();
        try{
            microservice.start();
            ServletFilter.executed=0;

            ClientConfig cc = new ClientConfig();
            Client client = ClientBuilder.newBuilder().withConfig(cc).build();
            Response response = client.target("http://localhost:8087/anything").request().buildGet().invoke();

            Assertions.assertEquals(1, ServletFilter.executed);
        } finally {
            microservice.stop();
        }
    }

    public static class ServletFilter implements Filter{
        static int executed=0;

        @Override
        public void init(FilterConfig filterConfig) {

        }

        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
            executed++;
        }

        @Override
        public void destroy() {

        }
    }

    public static class ServletFilterFactory extends SimpleFactoryBase<Filter, JettyServerRootFactory>{

        @Override
        protected Filter createImpl() {
            return new ServletFilter();
        }
    }

    @Test
    public void test_jersey_property(){
        JettyFactoryTreeBuilder builder = new JettyFactoryTreeBuilder((jetty, ctx)->{
            jetty.withHost("localhost").withPort(8015).withJerseyProperties(Map.of(ServerProperties.BV_SEND_ERROR_IN_RESPONSE,true)).withJaxrsComponent(new FactoryTemplateId<>(TestFeatureFactory.class));
        });
        builder.addSingleton(TestFeatureFactory.class);
        Microservice<Server, JettyServerRootFactory> microservice = builder.microservice().build();
        try{
            microservice.start();
            Assertions.assertEquals(true,TestFeature.propertyValue);
        } finally {
            microservice.stop();
        }
    }

    @Test
    public void test_jersey_property_false(){
        JettyFactoryTreeBuilder builder = new JettyFactoryTreeBuilder((jetty, ctx)->{
            jetty.withHost("localhost").withPort(8015).withJerseyProperties(Map.of(ServerProperties.BV_SEND_ERROR_IN_RESPONSE,false)).withJaxrsComponent(new FactoryTemplateId<>(TestFeatureFactory.class));
        });
        builder.addSingleton(TestFeatureFactory.class);
        Microservice<Server, JettyServerRootFactory> microservice = builder.microservice().build();
        try{
            microservice.start();
            Assertions.assertEquals(false,TestFeature.propertyValue);
        } finally {
            microservice.stop();
        }
    }

    public static class TestFeatureFactory extends SimpleFactoryBase<Feature, JettyServerRootFactory>{
        @Override
        protected Feature createImpl() {
            return new TestFeature();
        }
    }

    public static class TestFeature  implements Feature {
        static Boolean propertyValue;

        @Override
        public boolean configure(final FeatureContext context) {
            final Configuration config = context.getConfiguration();
            propertyValue = (Boolean) config.getProperty(ServerProperties.BV_SEND_ERROR_IN_RESPONSE) ;
            return true;
        }
    }






}