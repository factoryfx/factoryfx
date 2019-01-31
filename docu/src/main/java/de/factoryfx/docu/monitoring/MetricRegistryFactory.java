package de.factoryfx.docu.monitoring;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricRegistry;
import de.factoryfx.factory.SimpleFactoryBase;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.function.BiConsumer;

public class MetricRegistryFactory extends SimpleFactoryBase<MetricRegistry, ServerVisitor, RootFactory> {
    @Override
    public MetricRegistry createImpl() {
        return new MetricRegistry();
    }

    MetricRegistryFactory(){
        configLifeCycle().setRuntimeQueryExecutor(new BiConsumer<ServerVisitor, MetricRegistry>() {
            @Override
            public void accept(ServerVisitor serverVisitor, MetricRegistry metricRegistry) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                PrintStream ps = new PrintStream(baos,false, StandardCharsets.UTF_8);
                ConsoleReporter consoleReporter = ConsoleReporter.forRegistry(metricRegistry).outputTo(ps).build();
                consoleReporter.report();
                consoleReporter.stop();
                serverVisitor.jettyReport=new String(baos.toByteArray(), StandardCharsets.UTF_8);
            }
        });
    }
}
