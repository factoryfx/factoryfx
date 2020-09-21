package io.github.factoryfx.docu.update;

import io.github.factoryfx.factory.storage.DataUpdate;
import io.github.factoryfx.factory.builder.FactoryTreeBuilder;
import io.github.factoryfx.server.Microservice;

public class Main {

    public static void main(String[] args) {
        FactoryTreeBuilder< Root, RootFactory> builder = new FactoryTreeBuilder<>(RootFactory.class, ctx->{
            RootFactory root = new RootFactory();
            root.dummy.set("1");
            return root;
        });

        long start=System.currentTimeMillis();
        Microservice<Root,RootFactory> microservice = builder.microservice().build();
        microservice.start();

        //over 5000ms most time for the ExpensiveResource
        System.out.println(System.currentTimeMillis()-start);
        microservice.getRootLiveObject().printDummy();//1

        long updateStart=System.currentTimeMillis();
        DataUpdate<RootFactory> update = microservice.prepareNewFactory();
        update.root.dummy.set("2");
        microservice.updateCurrentFactory(update);

        //significantly less than the 5000ms => ExpensiveResource not recreated
        System.out.println(System.currentTimeMillis()-updateStart);
        microservice.getRootLiveObject().printDummy();//2

    }
}
