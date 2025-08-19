package io.github.factoryfx.jetty.ssl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Objects;
import java.util.Properties;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import io.github.factoryfx.factory.builder.FactoryTemplateId;
import org.eclipse.jetty.server.Server;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.builder.Scope;
import io.github.factoryfx.jetty.builder.JettyFactoryTreeBuilder;
import io.github.factoryfx.jetty.builder.JettyServerRootFactory;
import io.github.factoryfx.server.Microservice;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

public class SslContextFactoryFactoryTest {

    public static class TestResourceFactory extends SimpleFactoryBase<TestResource, JettyServerRootFactory> {

        @Override
        protected TestResource createImpl() {
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


    @Test
    public void test_without_ssl() {
        JettyFactoryTreeBuilder builder = new JettyFactoryTreeBuilder((jetty, ctx) -> jetty
                .withHost("localhost").withPort(8009).withResource(new FactoryTemplateId<>(TestResourceFactory.class)));

        builder.addFactory(TestResourceFactory.class, Scope.SINGLETON);

        Microservice<Server, JettyServerRootFactory> microservice = builder.microservice().build();
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

    @SuppressWarnings("unchecked")
    @Test
    public void test_with_ssl() {
        JettyFactoryTreeBuilder builder = new JettyFactoryTreeBuilder((jetty, ctx) -> jetty
                .withHost("localhost").withPort(8009).withResource(new FactoryTemplateId<>(TestResourceFactory.class)).withSsl(ctx.get(ServerSslContextFactoryFactory.class)));

        builder.addFactory(ServerSslContextFactoryFactory.class, Scope.SINGLETON, ctx->{
            ServerSslContextFactoryFactory<JettyServerRootFactory> ssl = new ServerSslContextFactoryFactory<>();
            ssl.keyStoreType.set(KeyStoreType.pkcs12);
            ssl.trustStoreType.set(KeyStoreType.pkcs12);
            try (InputStream in = getClass().getResourceAsStream("/keystore.p12")){
                byte[] bytes = in.readAllBytes();
                ssl.keyStore.set(bytes);
                ssl.keyStorePassword.set("password");
                ssl.trustStore.set(bytes);
                ssl.trustStorePassword.set("password");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            return ssl;
        });
        builder.addFactory(TestResourceFactory.class, Scope.SINGLETON);

        Microservice<Server, JettyServerRootFactory> microservice = builder.microservice().build();
        try {
            microservice.start();
            fixUntrustCertificate();
            URL url = new URL("https://localhost:8009/test");
            URLConnection conn = url.openConnection();
            InputStream is = conn.getInputStream();
            Assertions.assertEquals("Hello World", convertStreamToString(is));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            microservice.stop();
        }

    }

    public void fixUntrustCertificate()  {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
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

            HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new RuntimeException(e);
        }
    }

    static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }



    //java http client only with http2+ssl?  https://stackoverflow.com/questions/52513932/force-immediate-insecure-http2-connection-with-java-httpclient
    @Disabled // https://bugs.openjdk.java.net/browse/JDK-8213309 cnt disable host name verification, (jdk.internal.httpclient.disableHostnameVerification is orde depend, doen't work if other  HttpClient.newBuilder() before this)
    @Test
    public void test_http2() {
        JettyFactoryTreeBuilder builder = new JettyFactoryTreeBuilder((jetty, ctx) -> jetty.
                withHttp2().withHost("localhost").withPort(8009).withResource(new FactoryTemplateId<>(TestResourceFactory.class)).withSsl(ctx.get(ServerSslContextFactoryFactory.class)));


        builder.addFactory(ServerSslContextFactoryFactory.class, Scope.SINGLETON, ctx->{
            ServerSslContextFactoryFactory<JettyServerRootFactory> ssl = new ServerSslContextFactoryFactory<>();
            ssl.keyStoreType.set(KeyStoreType.jks);
            ssl.trustStoreType.set(KeyStoreType.jks);
            try (InputStream in = getClass().getResourceAsStream("/keystore.jks")){
                byte[] bytes = in.readAllBytes();
                ssl.keyStore.set(bytes);
                ssl.keyStorePassword.set("password");

                ssl.trustStore.set(bytes);
                ssl.trustStorePassword.set("password");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            return ssl;
        });
        builder.addFactory(TestResourceFactory.class, Scope.SINGLETON);

        Microservice<Server, JettyServerRootFactory> microservice = builder.microservice().build();
        try {
            microservice.start();

            {

                final Properties props = System.getProperties();
                props.setProperty("jdk.internal.httpclient.disableHostnameVerification", Boolean.TRUE.toString());

                TrustManager[] trustAllCerts = new TrustManager[]{
                        new X509TrustManager() {
                            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                                return null;
                            }
                            public void checkClientTrusted(
                                    java.security.cert.X509Certificate[] certs, String authType) {
                            }
                            public void checkServerTrusted(
                                    java.security.cert.X509Certificate[] certs, String authType) {
                            }
                        }
                };
                SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, trustAllCerts, new SecureRandom());

//                fixUntrustCertificate();
                HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).sslContext(sslContext).build();
                HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create("https://localhost:8009/test")).build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                Assertions.assertEquals("Hello World", response.body());
            }
//
        } catch (IOException | InterruptedException | NoSuchAlgorithmException | KeyManagementException e) {
            throw new RuntimeException(e);
        } finally {
            microservice.stop();
            final Properties props = System.getProperties();
            props.setProperty("jdk.internal.httpclient.disableHostnameVerification", Boolean.FALSE.toString());
        }

    }


    @SuppressWarnings("unchecked")
    @Test
    public void test_with_ssl_from_file() {
        JettyFactoryTreeBuilder builder = new JettyFactoryTreeBuilder((jetty, ctx) -> jetty
                .withHost("localhost").withPort(8009).withResource(new FactoryTemplateId<>(TestResourceFactory.class)).withSsl(ctx.get(ServerIniFileSslContextFactoryFactory.class)));

        builder.addFactory(ServerIniFileSslContextFactoryFactory.class, Scope.SINGLETON, ctx->{
            ServerIniFileSslContextFactoryFactory<JettyServerRootFactory> ssl = new ServerIniFileSslContextFactoryFactory<>();
            try {
                ssl.iniFile.set(Paths.get(Objects.requireNonNull(getClass().getResource("/server_ssl.ini")).toURI()).toAbsolutePath().toString());
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
            ssl.section.set("section1");
            return ssl;
        });
        builder.addFactory(TestResourceFactory.class, Scope.SINGLETON);

        Microservice<Server, JettyServerRootFactory> microservice = builder.microservice().build();
        try {
            microservice.start();
            fixUntrustCertificate();
            URL url = new URL("https://localhost:8009/test");
            URLConnection conn = url.openConnection();
            InputStream is = conn.getInputStream();
            Assertions.assertEquals("Hello World", convertStreamToString(is));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            microservice.stop();
        }

    }

}