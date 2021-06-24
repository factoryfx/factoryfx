package io.github.factoryfx.jetty.ssl;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.builder.Scope;
import io.github.factoryfx.jetty.builder.FactoryTemplateName;
import io.github.factoryfx.jetty.builder.JettyFactoryTreeBuilder;
import io.github.factoryfx.jetty.builder.JettyServerRootFactory;
import io.github.factoryfx.server.Microservice;
import org.eclipse.jetty.server.Server;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.net.ssl.*;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Properties;

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
                .withHost("localhost").withPort(8009).withResource(ctx.get(TestResourceFactory.class)));

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

    public static class SslContextFactoryFactoryCustom<R extends FactoryBase<?, R>> extends SslContextFactoryFactory<R> {
        public SslContextFactoryFactoryCustom(){
            trustStore.nullable();
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void test_with_ssl() {
        JettyFactoryTreeBuilder builder = new JettyFactoryTreeBuilder((jetty, ctx) -> jetty
                .withHost("localhost").withPort(8009).withResource(ctx.get(TestResourceFactory.class)).withSsl(ctx.get(SslContextFactoryFactoryCustom.class)));

        builder.addFactory(SslContextFactoryFactoryCustom.class, Scope.SINGLETON, ctx->{
            SslContextFactoryFactoryCustom<JettyServerRootFactory> ssl = new SslContextFactoryFactoryCustom<>();
            ssl.keyStoreType.set(KeyStoreType.jks);
            ssl.trustStoreType.set(KeyStoreType.jks);
            ssl.connectorType.set(SslContextFactoryFactory.ConnectorType.SERVER);
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
            fixUntrustCertificate();
            URL url = new URL("https://localhost:8009/test");
            URLConnection conn = url.openConnection();
            InputStream is = conn.getInputStream();
            Assertions.assertEquals("Hello World",convertStreamToString(is));
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

            HostnameVerifier allHostsValid = new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };

            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new RuntimeException(e);
        }
    }

    static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }



    //java http client only with http2+ssl?  https://stackoverflow.com/questions/52513932/force-immediate-insecure-http2-connection-with-java-httpclient
    @Test
    public void test_http2() {
        JettyFactoryTreeBuilder builder = new JettyFactoryTreeBuilder((jetty,ctx)->{
            jetty
                    .withResource(ctx.get(TestResourceFactory.class))
                    .setAdditionalConnector(
                            connector-> connector.withHost("localhost").withPort(8009).withHttp2().withSsl(ctx.get(SslContextFactoryFactoryCustom.class)),new FactoryTemplateName("connector1")
                    );

        });
        builder.addFactory(SslContextFactoryFactoryCustom.class, Scope.SINGLETON, ctx->{
            SslContextFactoryFactoryCustom<JettyServerRootFactory> ssl = new SslContextFactoryFactoryCustom<>();
            ssl.keyStoreType.set(KeyStoreType.jks);
            ssl.trustStoreType.set(KeyStoreType.jks);
            ssl.connectorType.set(SslContextFactoryFactory.ConnectorType.SERVER);
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
}