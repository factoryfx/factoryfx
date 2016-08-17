package de.factoryfx.adminui.angularjs.integration.example;

import java.util.List;

import de.factoryfx.factory.LiveObject;

public class ExampleLiveObjectA implements LiveObject<ExampleVisitor> {
    public ExampleLiveObjectA(ExampleLiveObjectB exampleLiveObjectB, List<ExampleLiveObjectB> exampleLiveObjectBs) {

    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void accept(ExampleVisitor visitor) {
        visitor.exampleDates.add(new ExampleData("a","b","c"));
        visitor.exampleDates.add(new ExampleData("a2","b2","c2"));
    }
}
