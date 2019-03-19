package io.github.factoryfx.factory.testfactories;

import java.util.List;

public class ExampleLiveObjectA{
    public final ExampleLiveObjectB exampleLiveObjectB;
    public final List<ExampleLiveObjectB> exampleLiveObjectBs;

    public ExampleLiveObjectA(ExampleLiveObjectB exampleLiveObjectB, List<ExampleLiveObjectB> exampleLiveObjectBs) {
        this.exampleLiveObjectB = exampleLiveObjectB;
        this.exampleLiveObjectBs = exampleLiveObjectBs;
    }

}
