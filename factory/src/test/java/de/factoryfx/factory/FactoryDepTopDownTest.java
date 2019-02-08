package de.factoryfx.factory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.factoryfx.data.storage.DataAndStoredMetadata;
import de.factoryfx.factory.builder.FactoryTreeBuilder;
import de.factoryfx.factory.builder.Scope;
import org.junit.Assert;
import org.junit.Test;

import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.factory.log.FactoryUpdateLog;
import de.factoryfx.server.Microservice;
import de.factoryfx.factory.builder.MicroserviceBuilder;

public class FactoryDepTopDownTest {

    private static List<String> creator = new ArrayList<>();
    private static List<String> recreator = new ArrayList<>();
    private static List<String> starter = new ArrayList<>();
    private static List<String> destroyer = new ArrayList<>();

    public static class ObjectFactory extends FactoryBase<Object, Void, RootFactory> {
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

    public static class RootFactory extends SimpleFactoryBase<String, Void, RootFactory> {
        public final FactoryReferenceAttribute<Object, ObjectFactory> object1 = new FactoryReferenceAttribute<>(ObjectFactory.class);
        public final FactoryReferenceAttribute<Object, ObjectFactory> object2 = new FactoryReferenceAttribute<>(ObjectFactory.class);

        @Override
        public String createImpl() {
            FactoryDepTopDownTest.creator.add(getId());
            return Stream.of(object1.instance(), object2.instance()).map(Object::toString).collect(Collectors.joining(", "));
        }
    }

    @Test
    public void topDownTest(){

        FactoryTreeBuilder<Void, String,RootFactory, Void> builder = new FactoryTreeBuilder<>(RootFactory.class);
        builder.addFactory(RootFactory.class, Scope.SINGLETON, ctx->{
            final RootFactory rootFactory = new RootFactory();
            ObjectFactory first = new ObjectFactory();
            ObjectFactory second = new ObjectFactory();
            rootFactory.object1.set(first);
            rootFactory.object2.set(second);
            return rootFactory;
        });


        Microservice<Void, String,RootFactory, Void> microService = builder.microservice().withInMemoryStorage().build();
        microService.start();

        DataAndStoredMetadata<RootFactory, Void> current = microService.prepareNewFactory();
        RootFactory rootFactory=current.root;
        ObjectFactory first=rootFactory.object1.get();
        ObjectFactory second=rootFactory.object2.get();
        {
            Assert.assertTrue(creator.contains(rootFactory.getId()));
            Assert.assertTrue(creator.contains(first.getId()));
            Assert.assertTrue(creator.contains(second.getId()));
            creator.clear();
            recreator.clear();
            starter.clear();
            destroyer.clear();

            System.out.println("update started");
            DataAndStoredMetadata<RootFactory,Void> update1 = microService.prepareNewFactory();
            ObjectFactory secondV2 = new ObjectFactory();
            update1.root.object2.set(secondV2);

            FactoryUpdateLog<RootFactory> res1 = microService.updateCurrentFactory(update1);
            Assert.assertTrue(res1.successfullyMerged());

            Assert.assertTrue(creator.contains(rootFactory.getId()));

            Assert.assertFalse(creator.contains(first.getId()));
            Assert.assertFalse(starter.contains(first.getId()));
            Assert.assertFalse(destroyer.contains(first.getId()));
            Assert.assertFalse(recreator.contains(first.getId()));

            Assert.assertTrue(destroyer.contains(second.getId()));
            Assert.assertTrue(creator.contains(secondV2.getId()));
            Assert.assertTrue(starter.contains(secondV2.getId()));
        }
    }

}
