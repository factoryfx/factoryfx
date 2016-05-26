package de.factoryfx.factory.testfactories;

import de.factoryfx.factory.LiveObject;

public class ExampleLiveObjectB implements LiveObject{
    private final ExampleLiveObjectC exampleLiveObjectC;

    public ExampleLiveObjectB(ExampleLiveObjectC exampleLiveObjectC) {
        this.exampleLiveObjectC = exampleLiveObjectC;
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }
}
