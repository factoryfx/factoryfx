package de.factoryfx.docu.dynamicwebserver;

import ch.qos.logback.classic.Level;
import de.factoryfx.factory.FactoryManager;
import de.factoryfx.data.storage.DataAndNewMetadata;
import de.factoryfx.data.storage.inmemory.InMemoryDataStorage;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.factory.exception.RethrowingFactoryExceptionHandler;
import de.factoryfx.server.ApplicationServer;
import de.factoryfx.server.rest.client.RestClient;
import de.factoryfx.server.rest.server.HttpServerConnectorFactory;
import de.factoryfx.server.rest.server.JettyServerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Collections;
import java.util.List;

public class Main {

    public static class DynamicWebserver extends JettyServerFactory<Void,RootFactory>{
        public final FactoryReferenceAttribute<WebResource,WebResourceFactory> resource = new FactoryReferenceAttribute<>();
        @Override
        protected List<Object> getResourcesInstances() {
            return Collections.singletonList(resource.instance());
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
        jettyServer=jettyServer.utility().prepareUsableCopy();


        ApplicationServer<Void,RootFactory,Void> applicationServer
                = new ApplicationServer<>(new FactoryManager<Void, RootFactory>(new RethrowingFactoryExceptionHandler<>()),new InMemoryDataStorage<>(jettyServer));
        applicationServer.start();

        Thread continuouslyQueryWebserver = startQueryServerThread();
        for (int i = 0; i < 10; ++i) {
            DataAndNewMetadata<RootFactory> editableConfig = applicationServer.prepareNewFactory();
            RootFactory editableJettyServer = editableConfig.root;
            editableJettyServer.server.get().resource.set(createNewWebResourceReturningCreationTimestamp());
            applicationServer.updateCurrentFactory(editableConfig,"user","commit",s->true);
            Thread.sleep(1100);
        }
        continuouslyQueryWebserver.interrupt();
        applicationServer.stop();

    }

    private static Thread startQueryServerThread() {
        RestClient restClient8005 = new RestClient("localhost",8005,"",false,null,null);
        Thread pollServer = new Thread() {
            {
                setDaemon(true);
            }
            public void run() {
                while (true) {
                    String serverResponse = restClient8005.get("/Resource",String.class);
                    System.out.println("Server responded: "+serverResponse);
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
