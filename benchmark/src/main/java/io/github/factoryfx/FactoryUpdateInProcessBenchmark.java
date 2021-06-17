package io.github.factoryfx;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.FactoryManager;
import io.github.factoryfx.factory.FactoryUpdate;
import io.github.factoryfx.factory.RootFactoryWrapper;
import io.github.factoryfx.factory.builder.FactoryTreeBuilder;
import io.github.factoryfx.factory.exception.RethrowingFactoryExceptionHandler;
import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;
import io.github.factoryfx.factory.storage.DataUpdate;
import io.github.factoryfx.factory.testfactories.*;
import io.github.factoryfx.server.Microservice;
import org.openjdk.jmh.annotations.*;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 *
 */
@Warmup(iterations = 3, time = 3, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 10, timeUnit = TimeUnit.SECONDS)
@Fork(2)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Threads(1)
public class FactoryUpdateInProcessBenchmark {

    private final Microservice<ExampleLiveObjectA, ExampleFactoryA> microservice;

    {
        {
            FactoryTreeBuilder<ExampleLiveObjectA,ExampleFactoryA> builder = new FactoryTreeBuilder<>(ExampleFactoryA.class, context -> {
                ExampleFactoryA factoryBases = new ExampleFactoryA();
                for (int i = 0; i < 10000; i++) {
                    factoryBases.referenceListAttribute.add(context.get(ExampleFactoryB.class));
                }
                return factoryBases;
            });
            builder.addFactory(ExampleFactoryB.class, io.github.factoryfx.factory.builder.Scope.SINGLETON, context -> {
                ExampleFactoryB factoryBases = new ExampleFactoryB();
                factoryBases.stringAttribute.set("123");
                return factoryBases;
            });
            microservice = builder.microservice().build();
            microservice.start();
        }
    }

    @Benchmark
    public void update_merge() {
        DataUpdate<ExampleFactoryA> update = microservice.prepareNewFactory();
        update.root.stringAttribute.set("22222"+Math.random());
        microservice.updateCurrentFactory(update);
    }

    @Benchmark
    public void update_in_process() {
        microservice.update((root, idToFactory) -> root.stringAttribute.set("22222"+Math.random()));
    }


    public static void main(String[] args) {
        FactoryUpdateInProcessBenchmark factoryUpdateBenchmark = new FactoryUpdateInProcessBenchmark();
        long start=System.currentTimeMillis();
        factoryUpdateBenchmark.update_merge();
        System.out.println("update_merge "+(System.currentTimeMillis()-start));
        start=System.currentTimeMillis();
        factoryUpdateBenchmark.update_in_process();
        System.out.println("update_in_process "+(System.currentTimeMillis()-start));
    }


}
