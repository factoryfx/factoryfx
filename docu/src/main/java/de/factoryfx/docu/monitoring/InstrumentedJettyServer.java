package de.factoryfx.docu.monitoring;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricRegistry;
import de.factoryfx.jetty.HttpServerConnectorCreator;
import de.factoryfx.jetty.JettyServer;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/*
    collect monitoring data with metrics library
 */
public class InstrumentedJettyServer{

    private final MetricRegistry metricRegistry;
    private JettyServer jettyServer;
    public InstrumentedJettyServer(JettyServer jettyServer, MetricRegistry metricRegistry) {
        this.jettyServer=jettyServer;
        this.metricRegistry=metricRegistry;
    }

    public void acceptVisitor(ServerVisitor serverVisitor){
        //MetricRegistry to string
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(baos,false, "UTF-8");
            ConsoleReporter consoleReporter = ConsoleReporter.forRegistry(metricRegistry).outputTo(ps).build();
            consoleReporter.report();
            consoleReporter.stop();
            serverVisitor.jettyReport=new String(baos.toByteArray(), StandardCharsets.UTF_8);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public void start() {
        jettyServer.start();
    }

    public void stop() {
        jettyServer.stop();
    }

    public InstrumentedJettyServer recreate(List<HttpServerConnectorCreator> instances, List<Object> simpleResources) {
        this.jettyServer=jettyServer.recreate(instances,simpleResources);
        return this;
    }
}
