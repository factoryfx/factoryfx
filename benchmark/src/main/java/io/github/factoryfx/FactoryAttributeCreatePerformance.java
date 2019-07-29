package io.github.factoryfx;

import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;
import io.github.factoryfx.factory.testfactories.*;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;


@Warmup(iterations = 3, time = 3, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 10, timeUnit = TimeUnit.SECONDS)
@Fork(2)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
@Threads(1)
public class FactoryAttributeCreatePerformance {

    public static class ExampleFactoryNullable extends SimpleFactoryBase<ExampleLiveObjectA, io.github.factoryfx.factory.testfactories.ExampleFactoryA> {
        public final FactoryAttribute<ExampleLiveObjectB,ExampleFactoryB> referenceAttribute = new FactoryAttribute<ExampleLiveObjectB,ExampleFactoryB>().nullable();

        @Override
        protected ExampleLiveObjectA createImpl() {
            return null;
        }
    }

    public static class ExampleFactoryNullableLambda extends SimpleFactoryBase<ExampleLiveObjectA, io.github.factoryfx.factory.testfactories.ExampleFactoryA> {
        public final FactoryAttribute<ExampleLiveObjectB,ExampleFactoryB> referenceAttribute = new FactoryAttribute<>(FactoryAttribute::nullable);

        @Override
        protected ExampleLiveObjectA createImpl() {
            return null;
        }
    }

    @Benchmark
    public void create() {
        for (int i = 0; i < 100000; i++) {
            new ExampleFactoryNullable();
        }
    }
    @Benchmark
    public void create_Lambda() {
        for (int i = 0; i < 100000; i++) {
            new ExampleFactoryNullableLambda();
        }
    }


}