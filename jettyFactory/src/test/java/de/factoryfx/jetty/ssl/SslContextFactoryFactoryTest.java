package de.factoryfx.jetty.ssl;

import com.google.common.io.ByteStreams;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.factory.builder.FactoryTreeBuilder;
import de.factoryfx.factory.builder.Scope;
import de.factoryfx.jetty.JettyServerBuilder;
import de.factoryfx.jetty.JettyServerFactory;
import de.factoryfx.server.Microservice;
import org.eclipse.jetty.server.Server;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.net.ssl.*;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

public class SslContextFactoryFactoryTest {

    public static class TestResourceFactory extends SimpleFactoryBase<TestResource,Void,TestJettyServerFactory> {

        @Override
        public TestResource createImpl() {
            return new TestResource();
        }
    }

    @Path("/test")
    public static class TestResource{
        @GET
        public Response get(){
            return Response.ok("Hello World").build();
        }

    }

    public static class TestJettyServerFactory extends SimpleFactoryBase<Server,Void, TestJettyServerFactory>{
        @SuppressWarnings("unchecked")
        public final FactoryReferenceAttribute<Server,JettyServerFactory<Void, TestJettyServerFactory>> server = FactoryReferenceAttribute.create(new FactoryReferenceAttribute<>(JettyServerFactory.class));

        @Override
        public Server createImpl() {
            return server.instance();
        }}

    @SuppressWarnings("unchecked")
    @Test
    public void test_without_ssl() {
        FactoryTreeBuilder<Void, Server, TestJettyServerFactory, Void> builder = new FactoryTreeBuilder<>(TestJettyServerFactory.class);
        builder.addFactory(TestJettyServerFactory.class, Scope.SINGLETON);
        builder.addFactory(JettyServerFactory.class, Scope.SINGLETON, ctx->{
            return new JettyServerBuilder<>(new JettyServerFactory<Void,TestJettyServerFactory>())
                    .withHost("localhost").widthPort(8009)
                    .withResource(ctx.get(TestResourceFactory.class)).build();
        });
        builder.addFactory(TestResourceFactory.class, Scope.SINGLETON);

        Microservice<Void, Server, TestJettyServerFactory, Void> microservice = builder.microservice().withInMemoryStorage().build();
        try {
            microservice.start();

            URL url = new URL("http://localhost:8009/test");
            URLConnection conn = url.openConnection();
            InputStream is = conn.getInputStream();
            Assertions.assertEquals("Hello World", convertStreamToString(is));

        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            microservice.stop();
        }
    }

    public static class SslContextFactoryFactoryCustom<V, R extends FactoryBase<?, V, R>> extends SslContextFactoryFactory<V, R> {
        SslContextFactoryFactoryCustom(){
            trustStore.nullable();
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void test_with_ssl() {
        FactoryTreeBuilder<Void, Server, TestJettyServerFactory, Void> builder = new FactoryTreeBuilder<>(TestJettyServerFactory.class);
        builder.addFactory(TestJettyServerFactory.class, Scope.SINGLETON);
        builder.addFactory(JettyServerFactory.class, Scope.SINGLETON, ctx->{
            SslContextFactoryFactoryCustom<Void,TestJettyServerFactory> ssl = new SslContextFactoryFactoryCustom<>();
            ssl.keyStoreType.set(KeyStoreType.jks);
            ssl.trustStoreType.set(KeyStoreType.jks);
            try (InputStream in = getClass().getResourceAsStream("/keystore.jks")){
                byte[] bytes = ByteStreams.toByteArray(in);
                ssl.keyStore.set(bytes);
                ssl.keyStorePassword.set("password");

                ssl.trustStore.set(bytes);
                ssl.trustStorePassword.set("password");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            return new JettyServerBuilder<>(new JettyServerFactory<Void,TestJettyServerFactory>())
                    .withHost("localhost").widthPort(8009).withSsl(ssl)
                    .withResource(ctx.get(TestResourceFactory.class)).build();
        });
        builder.addFactory(TestResourceFactory.class, Scope.SINGLETON);

        Microservice<Void, Server, TestJettyServerFactory, Void> microservice = builder.microservice().withInMemoryStorage().build();
        try {
            microservice.start();
            fixUntrustCertificate();
            URL url = new URL("https://localhost:8009/test");
            URLConnection conn = url.openConnection();
            InputStream is = conn.getInputStream();
            Assertions.assertEquals("Hello World",convertStreamToString(is));
        } catch (NoSuchAlgorithmException | KeyManagementException | IOException e) {
            throw new RuntimeException(e);
        } finally {
            microservice.stop();
        }

    }

    public void fixUntrustCertificate() throws KeyManagementException, NoSuchAlgorithmException {
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }

        }};
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

        HostnameVerifier allHostsValid = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };

        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
    }

    static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

}