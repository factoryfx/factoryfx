package de.factoryfx.factory.testfactories;

import java.util.List;

import de.factoryfx.factory.LiveObject;

public class ExampleLiveObjectA implements LiveObject{
    public final ExampleLiveObjectB exampleLiveObjectB;
    public final List<ExampleLiveObjectB> exampleLiveObjectBs;

    public ExampleLiveObjectA(ExampleLiveObjectB exampleLiveObjectB, List<ExampleLiveObjectB> exampleLiveObjectBs) {
        this.exampleLiveObjectB = exampleLiveObjectB;
        this.exampleLiveObjectBs = exampleLiveObjectBs;
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }
}
