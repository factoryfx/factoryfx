package io.github.factoryfx.docu.polymorphism;


import io.github.factoryfx.factory.storage.DataUpdate;
import io.github.factoryfx.factory.builder.FactoryTreeBuilder;
import io.github.factoryfx.server.Microservice;

public class Main {

    public static void main(String[] args) {
        FactoryTreeBuilder<Root,RootFactory> builder = new FactoryTreeBuilder<>(RootFactory.class, ctx->{
            RootFactory root = new RootFactory();
            root.printer.set(ctx.get(DefaultPrinterFactory.class));
            return root;
        });
        builder.addSingleton(DefaultPrinterFactory.class);


        Microservice<Root,RootFactory> microservice = builder.microservice().build();
        microservice.start();

        DataUpdate<RootFactory> update = microservice.prepareNewFactory();
        //update to print on error out
        update.root.printer.set(new ErrorPrinterFactory());
        microservice.updateCurrentFactory(update);

    }
}
