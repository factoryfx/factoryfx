package de.factoryfx.docu.monitoring;

import ch.qos.logback.classic.Level;
import de.factoryfx.factory.FactoryManager;
import de.factoryfx.data.storage.inmemory.InMemoryDataStorage;
import de.factoryfx.factory.builder.FactoryTreeBuilder;
import de.factoryfx.factory.builder.Scope;
import de.factoryfx.factory.exception.RethrowingFactoryExceptionHandler;
import de.factoryfx.jetty.JettyServerBuilder;
import de.factoryfx.jetty.JettyServerFactory;
import de.factoryfx.server.Microservice;
import org.eclipse.jetty.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Main {
    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws Exception {
        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.INFO);

        FactoryTreeBuilder<RootFactory> builder = new FactoryTreeBuilder<>(RootFactory.class);
        builder.addFactory(RootFactory.class, Scope.SINGLETON);
        builder.addFactory(JettyServerFactory.class, Scope.SINGLETON, ctx-> {
            JettyServerFactory<ServerVisitor, RootFactory> server = new JettyServerBuilder<>(new JettyServerFactory<ServerVisitor, RootFactory>()).withHost("localhost").widthPort(34576).withResource(ctx.get(SimpleResourceFactory.class)).build();
            server.handler.get().handlers.set(0,ctx.get(InstrumentedHandlerFactory.class));
            return server;
        });
        builder.addFactory(SimpleResourceFactory.class, Scope.SINGLETON);
        builder.addFactory(InstrumentedHandlerFactory.class, Scope.SINGLETON);
        builder.addFactory(MetricRegistryFactory.class, Scope.SINGLETON);

        Microservice<ServerVisitor, Server,RootFactory,Void> microservice = new Microservice<>(new FactoryManager<>(new RethrowingFactoryExceptionHandler()),new InMemoryDataStorage<>(builder.buildTree()));
        microservice.start();

        //execute some random request as example
        getHTML("http://localhost:34576");

        //for the sake of simplicity the query ist called in the same vm.
        //you could also use the microserviceRestServer / microserviceRestClient from a different process if ServerVisitor is json serializable
        ServerVisitor serverVisitor = new ServerVisitor();
        microservice.query(serverVisitor);
        System.out.println(serverVisitor.jettyReport);

        //report shows 1 get request
        //...
        //monitoring example.get-requests
        //     count = 1
    }

    public static String getHTML(String urlToRead) throws Exception {
        HttpClient httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(urlToRead)).GET().build();
        try {
            HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return httpResponse.body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
