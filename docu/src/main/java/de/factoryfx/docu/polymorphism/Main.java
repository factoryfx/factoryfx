package de.factoryfx.docu.polymorphism;


import de.factoryfx.data.storage.DataUpdate;
import de.factoryfx.factory.builder.FactoryTreeBuilder;
import de.factoryfx.factory.builder.Scope;
import de.factoryfx.server.Microservice;

public class Main {

    public static void main(String[] args) {
        FactoryTreeBuilder<Root,RootFactory,Void> builder = new FactoryTreeBuilder<>(RootFactory.class);
        builder.addFactory(RootFactory.class, Scope.SINGLETON, ctx->{
            RootFactory root = new RootFactory();
            root.printer.set(new DefaultPrinterFactory());
            return root;
        });

        Microservice<Root,RootFactory,Void> microservice = builder.microservice().withInMemoryStorage().build();
        microservice.start();

        DataUpdate<RootFactory> update = microservice.prepareNewFactory();
        //update to print on error out
        update.root.printer.set(new ErrorPrinterFactory());
        microservice.updateCurrentFactory(update);

    }
}
