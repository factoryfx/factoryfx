package io.github.factoryfx.factory.record;

import io.github.factoryfx.factory.builder.FactoryTreeBuilder;
import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;
import io.github.factoryfx.factory.storage.DataUpdate;
import io.github.factoryfx.server.Microservice;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class RecordFactoryTest {

    @Test
    void test_smoketest(){
        FactoryTreeBuilder<Root,RootFactory> builder = new FactoryTreeBuilder<>(RootFactory.class, ctx -> {
            RootFactory rootFactory = new RootFactory();
            rootFactory.recordB.set(ctx.get("record"));
            return rootFactory;
        });
        builder.addSingleton("record", ctx -> {
            return new RecordFactory<>(new RecordExampleB.Dep(
                    "test"
            ));
        });

        Microservice<Root,RootFactory> microservice = builder.microservice().build();
        //execute
        microservice.start().print();
        Assertions.assertEquals("test",microservice.start().print());

        DataUpdate<RootFactory> update = microservice.prepareNewFactory();
        //read
        Assertions.assertEquals("test",update.root.recordB.get().dep().stringAttribute());

        //update
        update.root.recordB.set(new RecordFactory<>(new RecordExampleB.Dep("test2")));
        microservice.updateCurrentFactory(update);
        Assertions.assertEquals("test2",microservice.getRootLiveObject().print());
    }

    @Test
    void test_dependency(){
        FactoryTreeBuilder<Root,RootFactory> builder = new FactoryTreeBuilder<>(RootFactory.class, ctx -> {
            RootFactory rootFactory = new RootFactory();
            rootFactory.recordA.set(ctx.get("recordA"));
            return rootFactory;
        });
        builder.addSingleton("recordA", ctx -> {
            RecordFactory<RecordExampleB,RecordExampleB.Dep,RootFactory> recordB = ctx.get("recordB");
            return new RecordFactory<>(new RecordExampleA.Dep(new Dependency<>(recordB),new DependencyList<>(List.of(recordB))));
        });
        builder.addSingleton("recordB", ctx -> {
            return new RecordFactory<>(new RecordExampleB.Dep(
                    "testB"
            ));
        });

        Microservice<Root,RootFactory> microservice = builder.microservice().build();
        //execute
        microservice.start().print();
        Assertions.assertEquals("testB",microservice.start().print());

        DataUpdate<RootFactory> update = microservice.prepareNewFactory();
        //read
        Assertions.assertEquals("testB",update.root.recordA.get().dep().exampleB().dep().stringAttribute());

        //update
        update.root.recordB.set(new RecordFactory<>(new RecordExampleB.Dep("testB2")));
        microservice.updateCurrentFactory(update);
        Assertions.assertEquals("testB2",microservice.getRootLiveObject().print());
    }

    @Test
    public void test_copy_value(){
        RecordFactory<RecordExampleB,RecordExampleB.Dep,RootFactory> factory = new RecordFactory<>(new RecordExampleB.Dep("testB2"));
        RecordFactory<RecordExampleB,RecordExampleB.Dep,RootFactory> copy = factory.utility().copy();
        Assertions.assertEquals("testB2",copy.dep().stringAttribute());

    }

    @Test
    public void test_copy_dependency(){
        RecordFactory<RecordExampleB,RecordExampleB.Dep,RootFactory> factoryB = new RecordFactory<>(new RecordExampleB.Dep("testB2"));
        RecordFactory<RecordExampleA,RecordExampleA.Dep,RootFactory> factoryA = new RecordFactory<>(new RecordExampleA.Dep(new Dependency<>(factoryB),new DependencyList<>(List.of(factoryB))));

        RecordFactory<RecordExampleA,RecordExampleA.Dep,RootFactory> copyA = factoryA.utility().copy();
        Assertions.assertNotNull(copyA.dep().exampleB().get());
        Assertions.assertNotEquals(factoryA.dep().exampleB().get(),copyA.dep().exampleB().get());
        Assertions.assertEquals(factoryA.dep().exampleB().get().getId(),copyA.dep().exampleB().get().getId());
        Assertions.assertEquals("testB2",copyA.dep().exampleB().dep().stringAttribute());

    }

    @Test
    public void test_json(){
        System.out.println(ObjectMapperBuilder.build().writeValueAsString(new RootFactory()));

        RecordFactory<RecordExampleB,RecordExampleB.Dep,RootFactory> factoryB = new RecordFactory<>(new RecordExampleB.Dep("testB2"));
        RecordFactory<RecordExampleA,RecordExampleA.Dep,RootFactory> factoryA = new RecordFactory<>(new RecordExampleA.Dep(new Dependency<>(factoryB),new DependencyList<>(List.of(factoryB))));

        System.out.println(ObjectMapperBuilder.build().writeValueAsString(factoryA));

//        RecordFactory<RecordExampleA, RecordExampleA.Dep, RootFactory> copy = ObjectMapperBuilder.build().copy(factoryA);
//        Assertions.assertNotNull(copy.dep().exampleB().dep());
    }

    @Test
    public void test_json_dep(){
        ObjectMapperBuilder.build().copy(new RecordExampleB.Dep("dsfdsf"));
    }


}
