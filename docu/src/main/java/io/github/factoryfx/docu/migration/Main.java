package io.github.factoryfx.docu.migration;

import io.github.factoryfx.factory.builder.FactoryTreeBuilder;

import java.io.IOException;
import java.nio.file.Files;

public class Main {

    public static void main(String[] args) throws IOException {

        FactoryTreeBuilder<Root,RootFactory,Void> builder = new FactoryTreeBuilder<>(RootFactory.class, ctx->{
            RootFactory rootFactory = new RootFactory();
            rootFactory.text.set("HelloWorld");
            return rootFactory;
        });
        builder.microservice().withRenameAttributeMigration(RootFactory.class,"previousAttributeName",(rf)->rf.text).//dummy rename for demonstration.
        withFilesystemStorage(Files.createTempDirectory("tempfiles")).build().start();

    }
}
