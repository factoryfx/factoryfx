package de.factoryfx.factory.testfactories;

import java.util.List;

import de.factoryfx.factory.LiveObject;

public class VoidLiveObject implements LiveObject<Void>{
    public final ExampleLiveObjectB exampleLiveObjectB;
    public final List<ExampleLiveObjectB> exampleLiveObjectBs;

    public VoidLiveObject(ExampleLiveObjectB exampleLiveObjectB, List<ExampleLiveObjectB> exampleLiveObjectBs) {
        this.exampleLiveObjectB = exampleLiveObjectB;
        this.exampleLiveObjectBs = exampleLiveObjectBs;
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void accept(Void visitor) {

    }
}
