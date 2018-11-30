package de.factoryfx.docu.monitoring;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricRegistry;
import de.factoryfx.jetty.HttpServerConnectorCreator;
import de.factoryfx.jetty.JettyServer;
import de.factoryfx.jetty.ServletBuilder;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
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
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos,false, StandardCharsets.UTF_8);
        ConsoleReporter consoleReporter = ConsoleReporter.forRegistry(metricRegistry).outputTo(ps).build();
        consoleReporter.report();
        consoleReporter.stop();
        serverVisitor.jettyReport=new String(baos.toByteArray(), StandardCharsets.UTF_8);
    }

    public void start() {
        jettyServer.start();
    }

    public void stop() {
        jettyServer.stop();
    }

    public InstrumentedJettyServer recreate(List<HttpServerConnectorCreator> instances, ServletBuilder servletBuilder) {
        this.jettyServer=jettyServer.recreate(instances,servletBuilder);
        return this;
    }
}
