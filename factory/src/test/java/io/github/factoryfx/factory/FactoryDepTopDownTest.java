package io.github.factoryfx.factory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.github.factoryfx.factory.storage.DataUpdate;
import io.github.factoryfx.factory.builder.FactoryTreeBuilder;
import io.github.factoryfx.factory.log.FactoryUpdateLog;
import io.github.factoryfx.server.Microservice;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;

public class FactoryDepTopDownTest {

    private static List<String> creator = new ArrayList<>();
    private static List<String> recreator = new ArrayList<>();
    private static List<String> starter = new ArrayList<>();
    private static List<String> destroyer = new ArrayList<>();

    public static class ObjectFactory extends FactoryBase<Object, RootFactory> {
        ObjectFactory() {
            configLifeCycle().setCreator(() -> {
                System.out.println("creator called " + getId());
                FactoryDepTopDownTest.creator.add(getId());
                return new Object();
            });
            configLifeCycle().setReCreator(old -> {
                System.out.println("reCreator called " + getId());
                FactoryDepTopDownTest.recreator.add(getId());
                return new Object();
            });
            configLifeCycle().setStarter(i -> {
                System.out.println("starter called " + getId());
                FactoryDepTopDownTest.starter.add(getId());
            });
            configLifeCycle().setDestroyer(i -> {
                System.out.println("destroyer called " + getId());
                FactoryDepTopDownTest.destroyer.add(getId());
            });
        }
    }

    public static class RootFactory extends SimpleFactoryBase<String, RootFactory> {
        public final FactoryAttribute<RootFactory,Object, ObjectFactory> object1 = new FactoryAttribute<>();
        public final FactoryAttribute<RootFactory,Object, ObjectFactory> object2 = new FactoryAttribute<>();

        @Override
        public String createImpl() {
            FactoryDepTopDownTest.creator.add(getId());
            return Stream.of(object1.instance(), object2.instance()).map(Object::toString).collect(Collectors.joining(", "));
        }
    }

    @Test
    public void topDownTest(){

        FactoryTreeBuilder< String,RootFactory, Void> builder = new FactoryTreeBuilder<>(RootFactory.class, ctx->{
            final RootFactory rootFactory = new RootFactory();
            ObjectFactory first = new ObjectFactory();
            ObjectFactory second = new ObjectFactory();
            rootFactory.object1.set(first);
            rootFactory.object2.set(second);
            return rootFactory;
        });


        Microservice<String,RootFactory, Void> microService = builder.microservice().withInMemoryStorage().build();
        microService.start();

        DataUpdate<RootFactory> current = microService.prepareNewFactory();
        RootFactory rootFactory=current.root;
        ObjectFactory first=rootFactory.object1.get();
        ObjectFactory second=rootFactory.object2.get();
        {
            Assertions.assertTrue(creator.contains(rootFactory.getId()));
            Assertions.assertTrue(creator.contains(first.getId()));
            Assertions.assertTrue(creator.contains(second.getId()));
            creator.clear();
            recreator.clear();
            starter.clear();
            destroyer.clear();

            System.out.println("update started");
            DataUpdate<RootFactory> update1 = microService.prepareNewFactory();
            ObjectFactory secondV2 = new ObjectFactory();
            update1.root.object2.set(secondV2);

            FactoryUpdateLog<RootFactory> res1 = microService.updateCurrentFactory(update1);
            Assertions.assertTrue(res1.successfullyMerged());

            Assertions.assertTrue(creator.contains(rootFactory.getId()));

            Assertions.assertFalse(creator.contains(first.getId()));
            Assertions.assertFalse(starter.contains(first.getId()));
            Assertions.assertFalse(destroyer.contains(first.getId()));
            Assertions.assertFalse(recreator.contains(first.getId()));

            Assertions.assertTrue(destroyer.contains(second.getId()));
            Assertions.assertTrue(creator.contains(secondV2.getId()));
            Assertions.assertTrue(starter.contains(secondV2.getId()));
        }
    }

}
