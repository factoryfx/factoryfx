package de.factoryfx.factory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import de.factoryfx.data.storage.DataUpdate;
import de.factoryfx.factory.builder.FactoryTreeBuilder;
import de.factoryfx.factory.builder.Scope;
import org.junit.Assert;
import org.junit.Test;

import de.factoryfx.factory.atrribute.FactoryReferenceListAttribute;
import de.factoryfx.factory.log.FactoryUpdateLog;
import de.factoryfx.server.Microservice;

public class FactoryReferenceListTest {

    private static List<String> creator = new ArrayList<>();
    private static List<String> recreator = new ArrayList<>();
    private static List<String> starter = new ArrayList<>();
    private static List<String> destroyer = new ArrayList<>();

    public static class ObjectFactory extends FactoryBase<Object, Void, RootFactory> {
        ObjectFactory() {
            configLifeCycle().setCreator(() -> {
                System.out.println("creator called " + getId());
                FactoryReferenceListTest.creator.add(getId());
                return new Object();
            });
            configLifeCycle().setReCreator(old -> {
                System.out.println("reCreator called " + getId());
                FactoryReferenceListTest.recreator.add(getId());
                return new Object();
            });
            configLifeCycle().setStarter(i -> {
                System.out.println("starter called " + getId());
                starter.add(getId());
            });
            configLifeCycle().setDestroyer(i -> {
                System.out.println("destroyer called " + getId());
                destroyer.add(getId());
            });
        }
    }

    public static class RootFactory extends SimpleFactoryBase<String, Void, RootFactory> {
        public final FactoryReferenceListAttribute<Object, ObjectFactory> objects = new FactoryReferenceListAttribute<>(ObjectFactory.class);

        @Override
        public String createImpl() {
            return objects.instances().stream().map(Object::toString).collect(Collectors.joining(", "));
        }
    }

    @Test
    public void referenceListTest() {
        ObjectFactory first = new ObjectFactory();

        FactoryTreeBuilder<Void, String,RootFactory, Void> builder = new FactoryTreeBuilder<>(RootFactory.class);
        builder.addFactory(RootFactory.class, Scope.SINGLETON, ctx->{
            RootFactory rootFactory = new RootFactory();
            rootFactory.objects.add(first);
            return rootFactory;
        });

        Microservice<Void, String,RootFactory, Void> microService = builder.microservice().withInMemoryStorage().build();
        microService.start();

        {
            Assert.assertTrue(creator.contains(first.getId()));
            creator.clear();
            recreator.clear();
            starter.clear();
            destroyer.clear();

            System.out.println("update started");
            DataUpdate<RootFactory> update1 = microService.prepareNewFactory();
            ObjectFactory second = new ObjectFactory();
            update1.root.objects.add(second);

            FactoryUpdateLog<RootFactory> res1 = microService.updateCurrentFactory(update1);
            Assert.assertTrue(res1.successfullyMerged());

             Assert.assertFalse(creator.contains(first.getId()));
             Assert.assertFalse(starter.contains(first.getId()));
             Assert.assertFalse(destroyer.contains(first.getId()));

            Assert.assertFalse(recreator.contains(first.getId()));
            Assert.assertTrue(creator.contains(second.getId()));
            Assert.assertTrue(starter.contains(second.getId()));
        }

    }

}
