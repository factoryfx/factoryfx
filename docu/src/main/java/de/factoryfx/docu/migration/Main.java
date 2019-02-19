package de.factoryfx.docu.migration;

import de.factoryfx.factory.builder.FactoryTreeBuilder;
import de.factoryfx.factory.builder.Scope;

import java.io.IOException;
import java.nio.file.Files;

public class Main {

    public static void main(String[] args) throws IOException {

        FactoryTreeBuilder<Void,Root,RootFactory,Void> builder = new FactoryTreeBuilder<>(RootFactory.class);
        builder.addFactory(RootFactory.class, Scope.SINGLETON, ctx->{
            RootFactory rootFactory = new RootFactory();
            rootFactory.text.set("HelloWorld");
            return rootFactory;
        });
        builder.microservice().
                withDataMigration(
                        (dmm)->dmm.renameAttribute(RootFactory.class,"previousAttributeName",(rf)->rf.text)//dummy rename for demonstration
                ).
        withFilesystemStorage(Files.createTempDirectory("tempfiles")).build().start();

    }
}
