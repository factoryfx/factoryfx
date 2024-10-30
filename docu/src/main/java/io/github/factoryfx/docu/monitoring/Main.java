package io.github.factoryfx.docu.monitoring;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import io.github.factoryfx.factory.builder.FactoryTreeBuilder;
import io.github.factoryfx.jetty.builder.SimpleJettyServerBuilder;
import io.github.factoryfx.server.Microservice;

public class Main {

    public static void main(String[] args) throws Exception {
        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.INFO);

        FactoryTreeBuilder<Root,RootFactory> builder = new FactoryTreeBuilder<>(RootFactory.class);
        builder.addBuilder(ctx->new SimpleJettyServerBuilder<RootFactory>().withHost("localhost").withPort(34576)
                .withResource(ctx.get(SimpleResourceFactory.class))
                .withHandlerFirst(ctx.get(InstrumentedHandlerFactory.class)));

        builder.addSingleton(SimpleResourceFactory.class);
        builder.addSingleton(InstrumentedHandlerFactory.class);
        builder.addSingleton(MetricRegistryFactory.class);

        Microservice<Root,RootFactory> microservice = builder.microservice().build();
        microservice.start();

        //execute some random request as example
        getHTML("http://localhost:34576");

        //for the sake of simplicity the query ist called in the same vm.
        //you could also use the jettyserver and jersey resource from a different process
        System.out.println(microservice.getRootLiveObject().report());



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
