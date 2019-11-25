package io.github.factoryfx;

import org.openjdk.jmh.generators.BenchmarkProcessor;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.CommandLineOptionException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

public class Main {

// If yes, is annotation processing enabled in your IDE? You can find the checkbox under
//Preferences -> Build, Execution, Deployment -> Compiler -> Annotation Processors

// doesn't work with module-info file

    public static void main(String[] args) throws CommandLineOptionException, RunnerException {

//        CommandLineOptions cmdOptions = new CommandLineOptions(args);

        Options opt = new OptionsBuilder()
                .include(IteratePerformance.class.getSimpleName())
                .forks(1)
                .build();

        Runner runner = new Runner(opt);

        runner.run();

        new BenchmarkProcessor();
    }
}
