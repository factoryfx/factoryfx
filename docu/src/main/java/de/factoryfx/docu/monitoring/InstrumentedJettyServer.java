package de.factoryfx.docu.monitoring;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.jetty9.InstrumentedHandler;
import de.factoryfx.server.rest.server.HttpServerConnectorCreator;
import de.factoryfx.server.rest.server.JettyServer;
import org.eclipse.jetty.server.Handler;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
    collect monitoring data with metrics library
 */
public class InstrumentedJettyServer extends JettyServer{

    private MetricRegistry metricRegistry;
    public InstrumentedJettyServer(List<HttpServerConnectorCreator> connectors, List<Object> resources) {
        super(connectors, resources);
    }

    @Override
    protected List<Handler> additionalHandlers(){
        metricRegistry= new MetricRegistry();
        return Arrays.asList(new InstrumentedHandler(metricRegistry,"monitoring example"));
    }

    public void acceptVisitor(ServerVisitor serverVisitor){
        //MetricRegistry to string
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        ConsoleReporter consoleReporter = ConsoleReporter.forRegistry(metricRegistry).outputTo(ps).build();
        consoleReporter.report();
        consoleReporter.stop();
        serverVisitor.jettyReport=new String(baos.toByteArray(), StandardCharsets.UTF_8);
    }
}
