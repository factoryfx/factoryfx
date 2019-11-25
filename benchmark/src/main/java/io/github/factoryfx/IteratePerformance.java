package io.github.factoryfx;

import io.github.factoryfx.factory.AttributeVisitor;
import io.github.factoryfx.factory.attribute.Attribute;
import io.github.factoryfx.factory.metadata.AttributeMetadata;
import io.github.factoryfx.factory.testfactories.ExampleFactoryA;
import io.github.factoryfx.factory.testfactories.ExampleFactoryB;
import io.github.factoryfx.factory.testfactories.FastExampleFactoryA;
import io.github.factoryfx.factory.testfactories.FastExampleFactoryB;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;


@Warmup(iterations = 3, time = 3, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 10, timeUnit = TimeUnit.SECONDS)
@Fork(2)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
@Threads(1)
public class IteratePerformance {

    ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
    FastExampleFactoryA fastExampleFactoryA = new FastExampleFactoryA();

    AttributeVisitor attributeVisitor = (attributeMetadata, attribute) -> {};
    AttributeVisitor attributeVisitorFast = (attributeMetadata, attribute) -> {};

    @Benchmark
    public void iterateAttributes() {
        exampleFactoryA.internal().visitAttributesFlat(attributeVisitor);
    }

    @Benchmark
    public void iterateAttributes_fast() {
        fastExampleFactoryA.internal().visitAttributesFlat(attributeVisitorFast);
    }


    public static void main(String[] args) {
        new IteratePerformance().iterateAttributes();
    }

}