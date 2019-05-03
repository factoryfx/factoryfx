package io.github.factoryfx.docu.initializr;

import ch.qos.logback.classic.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ServerMainCustomized {
  public static void main(String[] args) {
    ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
    root.setLevel(Level.INFO);

    new ServerBuilder().builder().microservice().build().start();

    HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();
    HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080")).GET().build();
    try {
      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
      System.out.println("Server responded: "+response.body());
    } catch (IOException | InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}
