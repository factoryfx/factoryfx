package io.github.factoryfx;

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
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Threads(1)
public class FactoryCopyPerformance {

    ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
    FastExampleFactoryA fastExampleFactoryA = new FastExampleFactoryA();
    {
        {
            exampleFactoryA.stringAttribute.set("dfssfdsfdsfd");
            exampleFactoryA.referenceAttribute.set(createExampleDataB(0));
            exampleFactoryA.referenceListAttribute.add(createExampleDataB(0));
        }


        {
            fastExampleFactoryA.stringAttribute="dfssfdsfdsfd";
            fastExampleFactoryA.referenceAttribute=createFastExampleDataB(0);
            fastExampleFactoryA.referenceListAttribute.add(createFastExampleDataB(0));
        }
    }

    @Benchmark
    public void copy() {
        exampleFactoryA.internal().copy();
    }
    @Benchmark
    public void copy_fast_Factories() {
        fastExampleFactoryA.internal().copy();
    }

    private static ExampleFactoryB createExampleDataB(int deep){
        if (deep>20){
            return null;
        }
        ExampleFactoryB exampleDataB = new ExampleFactoryB();
        exampleDataB.referenceAttribute.set(createExampleDataA(++deep));
        return exampleDataB;
    }

    private static ExampleFactoryA createExampleDataA(int deep){
        if (deep>20){
            return null;
        }
        ExampleFactoryA exampleDataA = new ExampleFactoryA();
        exampleDataA.referenceAttribute.set(createExampleDataB(++deep));
        for (int i=0;i<5;i++){
            ExampleFactoryB value = createExampleDataB(++deep);
            if (value!=null){
                exampleDataA.referenceListAttribute.add(value);
            }
        }
        return exampleDataA;
    }


    private static FastExampleFactoryB createFastExampleDataB(int deep){
        if (deep>20){
            return null;
        }
        FastExampleFactoryB exampleDataB = new FastExampleFactoryB();
        exampleDataB.referenceAttribute=createFastExampleDataA(++deep);
        return exampleDataB;
    }

    private static FastExampleFactoryA createFastExampleDataA(int deep){
        if (deep>20){
            return null;
        }
        FastExampleFactoryA exampleDataA = new FastExampleFactoryA();
        exampleDataA.referenceAttribute=createFastExampleDataB(++deep);
        for (int i=0;i<5;i++){
            FastExampleFactoryB value = createFastExampleDataB(++deep);
            if (value!=null){
                exampleDataA.referenceListAttribute.add(value);
            }
        }
        return exampleDataA;
    }


    public static void main(String[] args) {
        new FactoryCopyPerformance().copy();
    }

}