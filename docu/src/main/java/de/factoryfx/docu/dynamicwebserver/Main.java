package de.factoryfx.docu.dynamicwebserver;

import ch.qos.logback.classic.Level;
import de.factoryfx.factory.FactoryManager;
import de.factoryfx.data.storage.DataAndNewMetadata;
import de.factoryfx.data.storage.inmemory.InMemoryDataStorage;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.factory.exception.RethrowingFactoryExceptionHandler;
import de.factoryfx.jetty.HttpServerConnectorFactory;
import de.factoryfx.jetty.JettyServer;
import de.factoryfx.jetty.JettyServerFactory;
import de.factoryfx.jetty.ServletBuilder;
import de.factoryfx.server.Microservice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatterBuilder;

public class Main {

    public static class DynamicWebserver extends JettyServerFactory<Void,RootFactory> {
        public final FactoryReferenceAttribute<WebResource,WebResourceFactory> resource = new FactoryReferenceAttribute<>(WebResourceFactory.class);

        @Override
        protected void setupServlets(ServletBuilder servletBuilder) {
            defaultSetupServlets(servletBuilder,resource.instance());
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.INFO);

        RootFactory jettyServer = new RootFactory();
        jettyServer.server.set(new DynamicWebserver());

        HttpServerConnectorFactory<Void, RootFactory> serverConnectorFactory = new HttpServerConnectorFactory<>();
        serverConnectorFactory.host.set("localhost");
        serverConnectorFactory.port.set(8005);
        jettyServer.server.get().connectors.add(serverConnectorFactory);

        jettyServer.server.get().resource.set(createNewWebResourceReturningCreationTimestamp());


        Microservice<Void,JettyServer,RootFactory,Void> microservice
                = new Microservice<>(new FactoryManager<>(new RethrowingFactoryExceptionHandler()),new InMemoryDataStorage<>(jettyServer));
        microservice.start();

        Thread continuouslyQueryWebserver = startQueryServerThread();
        for (int i = 0; i < 10; ++i) {
            DataAndNewMetadata<RootFactory> editableConfig = microservice.prepareNewFactory();
            RootFactory editableJettyServer = editableConfig.root;
            editableJettyServer.server.get().resource.set(createNewWebResourceReturningCreationTimestamp());
            microservice.updateCurrentFactory(editableConfig,"user","commit", s->true);
            Thread.sleep(1100);
        }
        continuouslyQueryWebserver.interrupt();
        microservice.stop();

    }

    private static Thread startQueryServerThread() {
        Thread pollServer = new Thread() {
            {
                setDaemon(true);
            }
            public void run() {
                while (true) {
                    HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();

                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create("http://localhost:8005/Resource"))
                            .GET()
                            .build();
                    try {
                        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                        System.out.println("Server responded: "+response.body());
                    } catch (IOException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }
        };
        pollServer.start();
        return pollServer;
    }


    private static WebResourceFactory createNewWebResourceReturningCreationTimestamp() {
        String time = new DateTimeFormatterBuilder().appendPattern("dd.MM.yyyy HH:mm:ss.SSS").toFormatter().format(LocalDateTime.now());
        WebResourceFactory webResourceFactory = new WebResourceFactory();
        webResourceFactory.responseText.set("Resource Factory was created at "+time);
        return webResourceFactory;
    }
}
