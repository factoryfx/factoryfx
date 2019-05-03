package io.github.factoryfx;

import io.github.factoryfx.factory.FactoryManager;
import io.github.factoryfx.factory.RootFactoryWrapper;
import io.github.factoryfx.factory.exception.RethrowingFactoryExceptionHandler;
import io.github.factoryfx.factory.testfactories.*;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

/**
 *
 */
@Warmup(iterations = 3, time = 3, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10, time = 10, timeUnit = TimeUnit.SECONDS)
@Fork(2)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Threads(1)
public class FactoryUpdateBenchmark {

    private final FactoryManager<ExampleLiveObjectA, FastExampleFactoryA> fastFactoryManager;
    private final FastExampleFactoryA fastCommonVersion;
    private final FastExampleFactoryA fastNewVersion;

    private final FactoryManager<ExampleLiveObjectA, ExampleFactoryA> factoryManager;
    ExampleFactoryA commonVersion;
    ExampleFactoryA newVersion;


    {
        {
            fastFactoryManager = new FactoryManager<>(new RethrowingFactoryExceptionHandler());

            FastExampleFactoryA root = new FastExampleFactoryA();
            for (int i = 0; i < 100000; i++) {
                FastExampleFactoryB factoryBases = new FastExampleFactoryB();
                factoryBases.referenceAttributeC = new FastExampleFactoryC();
                root.referenceListAttribute.add(factoryBases);
            }

            fastCommonVersion = root.internal().copy();
            fastNewVersion = root.internal().copy();

            fastFactoryManager.start(new RootFactoryWrapper<>(root));
        }

        {
            factoryManager = new FactoryManager<>(new RethrowingFactoryExceptionHandler());

            ExampleFactoryA root = new ExampleFactoryA();
            for (int i=0;i<100000;i++){
                ExampleFactoryB factoryBases = new ExampleFactoryB();
                factoryBases.referenceAttributeC.set(new ExampleFactoryC());
                root.referenceListAttribute.add(factoryBases);
            }

            commonVersion = root.internal().copy();
            newVersion = root.internal().copy();

            factoryManager.start(new RootFactoryWrapper<>(root));
        }

    }

    @Benchmark
    public void fast_factories() {
        fastFactoryManager.update(fastCommonVersion, fastNewVersion, (p) -> true);
    }

    @Benchmark
    public void attribute_factories() {
        factoryManager.update(commonVersion, newVersion, (p) -> true);
    }


    public static void main(String[] args) {
        new FactoryUpdateBenchmark().fast_factories();
    }


}
