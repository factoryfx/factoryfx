package de.factoryfx.factory.testfactories;

import de.factoryfx.factory.LiveObject;

public class ExampleLiveObjectB implements LiveObject{
    private final ExampleLiveObjectA exampleLiveObjectA;

    public ExampleLiveObjectB(ExampleLiveObjectA exampleLiveObjectA) {
        this.exampleLiveObjectA = exampleLiveObjectA;
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }
}
