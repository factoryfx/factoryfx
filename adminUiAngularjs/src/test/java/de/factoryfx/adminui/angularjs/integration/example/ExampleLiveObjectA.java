package de.factoryfx.adminui.angularjs.integration.example;

import java.util.List;

import de.factoryfx.factory.LifecycleNotifier;

public class ExampleLiveObjectA {
    public ExampleLiveObjectA(ExampleLiveObjectB exampleLiveObjectB, List<ExampleLiveObjectB> exampleLiveObjectBs, LifecycleNotifier<ExampleVisitor> lifecycleNotifier) {
        lifecycleNotifier.setRuntimeQueryConsumer(visitor -> {
            visitor.exampleDates.add(new ExampleData("a","b","c"));
            visitor.exampleDates.add(new ExampleData("a2","b2","c2"));
        });
    }

}
