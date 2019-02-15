package de.factoryfx.docu.runtimestatus;

import de.factoryfx.data.storage.DataUpdate;
import de.factoryfx.factory.builder.FactoryTreeBuilder;
import de.factoryfx.factory.builder.Scope;
import de.factoryfx.server.Microservice;

public class Main {

    public static void main(String[] args) {
        FactoryTreeBuilder<Void, Root, RootFactory,Void> builder = new FactoryTreeBuilder<>(RootFactory.class);
        builder.addFactory(RootFactory.class, Scope.SINGLETON, ctx->{
            RootFactory root = new RootFactory();
            root.stringAttribute.set("1");
            return root;
        });

        long start=System.currentTimeMillis();
        Microservice<Void, Root, RootFactory,Void> microservice = builder.microservice().withInMemoryStorage().build();
        microservice.start();

        //over 5000ms most time for the ExpensiveResource
        System.out.println(System.currentTimeMillis()-start);

        long updateStart=System.currentTimeMillis();
        DataUpdate<RootFactory> update = microservice.prepareNewFactory();
        update.root.stringAttribute.set("2");
        microservice.updateCurrentFactory(update);

        //much less than the 5000ms => ExpensiveResource not recreated
        System.out.println(System.currentTimeMillis()-updateStart);

    }
}
