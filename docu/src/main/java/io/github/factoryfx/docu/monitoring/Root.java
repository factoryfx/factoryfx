package io.github.factoryfx.docu.monitoring;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricRegistry;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

public class Root {
    private final MetricRegistry metricRegistry;

    public Root(MetricRegistry metricRegistry) {
        this.metricRegistry = metricRegistry;
    }

    String report(){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos,false, StandardCharsets.UTF_8);
        ConsoleReporter consoleReporter = ConsoleReporter.forRegistry(metricRegistry).outputTo(ps).build();
        consoleReporter.report();
        consoleReporter.stop();
        return new String(baos.toByteArray(), StandardCharsets.UTF_8);
    }
}
