package io.github.factoryfx;

import io.github.factoryfx.factory.merge.DataMerger;
import io.github.factoryfx.factory.testfactories.*;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

@Warmup(iterations = 3, time = 3, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 10, timeUnit = TimeUnit.SECONDS)
@Fork(2)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Threads(1)
public class MergeBenchmark {
    ExampleFactoryA currentModel;
    ExampleFactoryA originalModel;
    ExampleFactoryA newModel;

    FastExampleFactoryA currentModelFast;
    FastExampleFactoryA originalModelFast;
    FastExampleFactoryA newModelFast;

    {
        {
            currentModel = new ExampleFactoryA();
            for (int i = 0; i < 100000; i++) {
                ExampleFactoryB dataB = new ExampleFactoryB();
                dataB.referenceAttributeC.set(new ExampleFactoryC());
                currentModel.referenceListAttribute.add(dataB);
            }
            currentModel.stringAttribute.set("1111111");

            currentModel.internal().finalise();
            originalModel = currentModel.internal().copy();
            newModel = currentModel.internal().copy();
        }


        {
            currentModelFast = new FastExampleFactoryA();
            for (int i = 0; i < 100000; i++) {
                FastExampleFactoryB dataB = new FastExampleFactoryB();
                dataB.referenceAttributeC=new FastExampleFactoryC();
                currentModelFast.referenceListAttribute.add(dataB);
            }
            currentModelFast.stringAttribute="1111111";

            currentModelFast.internal().finalise();
            originalModelFast = currentModelFast.internal().copy();
            newModelFast = currentModelFast.internal().copy();
        }

    }

    @Benchmark
    public void test_performance_merge(){
        newModel.stringAttribute.set("33535");
        DataMerger<ExampleFactoryA> dataMerger = new DataMerger<>(currentModel, originalModel, newModel);
        dataMerger.mergeIntoCurrent((permission)->true);
    }

    @Benchmark
    public void test_performance_merge_fast_factories(){
        newModel.stringAttribute.set("33535");
        DataMerger<FastExampleFactoryA> dataMerger = new DataMerger<>(currentModelFast, originalModelFast, newModelFast);
        dataMerger.mergeIntoCurrent((permission)->true);
    }

    public static void main(String[] args) {
       new MergeBenchmark().test_performance_merge_fast_factories();
    }

}
