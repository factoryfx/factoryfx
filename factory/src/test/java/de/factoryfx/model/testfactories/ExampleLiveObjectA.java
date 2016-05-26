package de.factoryfx.model.testfactories;

import java.util.List;

import de.factoryfx.model.LiveObject;

public class ExampleLiveObjectA implements LiveObject{
    public final ExampleLiveObjectB exampleLiveObjectB;
    public final List<ExampleLiveObjectB> exampleLiveObjectBs;

    public ExampleLiveObjectA(ExampleLiveObjectB exampleLiveObjectB, List<ExampleLiveObjectB> exampleLiveObjectBs) {
        this.exampleLiveObjectB = exampleLiveObjectB;
        this.exampleLiveObjectBs = exampleLiveObjectBs;
    }

    @Override
    public void prepareStart() {

    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }
}
