package de.factoryfx.docu.dynamicwebserver;

import ch.qos.logback.classic.Level;
import de.factoryfx.docu.lifecycle.Root;
import de.factoryfx.docu.lifecycle.RootFactory;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.FactoryManager;
import de.factoryfx.factory.datastorage.FactoryAndNewMetadata;
import de.factoryfx.factory.datastorage.inmemory.InMemoryFactoryStorage;
import de.factoryfx.factory.exception.RethrowingFactoryExceptionHandler;
import de.factoryfx.server.ApplicationServer;
import de.factoryfx.server.rest.client.RestClient;
import de.factoryfx.server.rest.server.HttpServerConnectorFactory;
import de.factoryfx.server.rest.server.JettyServer;
import de.factoryfx.server.rest.server.JettyServerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatterBuilder;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.INFO);

        JettyServerFactory<Void> jettyServer = new JettyServerFactory<>();

        HttpServerConnectorFactory<Void> serverConnectorFactory = new HttpServerConnectorFactory<>();
        serverConnectorFactory.host.set("localhost");
        serverConnectorFactory.port.set(8005);
        jettyServer.connectors.get().add(serverConnectorFactory);

        jettyServer.resources.add(createNewWebResourceReturningCreationTimestamp());


        ApplicationServer<Void,JettyServer,JettyServerFactory<Void>> applicationServer
                = new ApplicationServer<>(new FactoryManager<>(new RethrowingFactoryExceptionHandler<>()),new InMemoryFactoryStorage<>(jettyServer));
        applicationServer.start();

        Thread continuouslyQueryWebserver = startQueryServerThread();
        for (int i = 0; i < 10; ++i) {
            FactoryAndNewMetadata<JettyServerFactory<Void>> editableConfig = applicationServer.prepareNewFactory();
            JettyServerFactory<Void> editableJettyServer = editableConfig.root;
            editableJettyServer.resources.clear();
            editableJettyServer.resources.add(createNewWebResourceReturningCreationTimestamp());
            applicationServer.updateCurrentFactory(editableConfig,"user","password",s->true);
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


    private static FactoryBase<?, Void> createNewWebResourceReturningCreationTimestamp() {
        String time = new DateTimeFormatterBuilder().appendPattern("dd.MM.yyyy HH:mm:ss.SSS").toFormatter().format(LocalDateTime.now());
        WebResourceFactory webResourceFactory = new WebResourceFactory();
        webResourceFactory.responseText.set("Resource Factory was created at "+time);
        return webResourceFactory;
    }
}
