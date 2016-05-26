package de.factoryfx.model.testfactories;

import de.factoryfx.model.LiveObject;

public class ExampleLiveObjectB implements LiveObject{
    private final ExampleLiveObjectA exampleLiveObjectA;

    public ExampleLiveObjectB(ExampleLiveObjectA exampleLiveObjectA) {
        this.exampleLiveObjectA = exampleLiveObjectA;
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
