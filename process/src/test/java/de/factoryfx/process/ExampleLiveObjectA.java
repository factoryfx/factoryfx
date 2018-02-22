package de.factoryfx.process;

import java.util.List;

public class ExampleLiveObjectA {

    public ExampleLiveObjectA(ProcessExecutor<ExampleProcess, ExampleProcessParameter> instance) {

        instance.create(new ExampleProcessParameter()).run();

    }

}
