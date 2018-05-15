package de.factoryfx.jetty.ssl;

import com.google.common.io.ByteStreams;
import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.jetty.HttpServerConnectorFactory;
import de.factoryfx.jetty.JettyServer;
import de.factoryfx.jetty.JettyServerFactory;
import org.junit.Assert;
import org.junit.Test;

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
import java.util.Arrays;
import java.util.List;

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



    public static class TestJettyServerFactory extends JettyServerFactory<Void,TestJettyServerFactory> {
        public final FactoryReferenceAttribute<TestResource,TestResourceFactory> resource = new FactoryReferenceAttribute<>();

        @Override
        protected List<Object> getResourcesInstances() {
            return Arrays.asList(resource.instance());
        }


    }

    @Test
    public void test_without_ssl() throws IOException {
        TestJettyServerFactory jettyServerFactory = new TestJettyServerFactory();
        jettyServerFactory.resource.set(new TestResourceFactory());
        HttpServerConnectorFactory<Void,TestJettyServerFactory> http = new HttpServerConnectorFactory<>();
        http.host.set("localhost");
        http.port.set(8009);
        jettyServerFactory.connectors.add(http);

        JettyServer server = jettyServerFactory.internalFactory().instance();
        server.start();


        URL url = new URL("http://localhost:8009/test");
        URLConnection conn = url.openConnection();
        InputStream is = conn.getInputStream();
        Assert.assertEquals("Hello World",convertStreamToString(is));

        server.stop();
    }

    @Test
    public void test_with_ssl() throws Exception {
        TestJettyServerFactory jettyServerFactory = new TestJettyServerFactory();
        jettyServerFactory.resource.set(new TestResourceFactory());
        HttpServerConnectorFactory<Void,TestJettyServerFactory> http = new HttpServerConnectorFactory<>();
        http.host.set("localhost");
        http.port.set(8009);
        jettyServerFactory.connectors.add(http);


        SslContextFactoryFactory<Void,TestJettyServerFactory> ssl = new SslContextFactoryFactory<>();
        ssl.keyStoreType.set(KeyStoreType.jks);
        ssl.trustStoreType.set(KeyStoreType.jks);
        try (InputStream in = getClass().getResourceAsStream("/keystore.jks")){
            byte[] bytes = ByteStreams.toByteArray(in);
            ssl.keyStore.set(bytes);
            ssl.keyStorePassword.set("password");

            ssl.trustStore.set(bytes);
            ssl.trustStorePassword.set("password");
        }

        http.ssl.set(ssl);

        JettyServer server = jettyServerFactory.internalFactory().instance();
        server.start();

        fixUntrustCertificate();
        URL url = new URL("https://localhost:8009/test");
        URLConnection conn = url.openConnection();
        InputStream is = conn.getInputStream();
        Assert.assertEquals("Hello World",convertStreamToString(is));

        server.stop();
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