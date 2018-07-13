package de.factoryfx.factory;

import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;

import de.factoryfx.data.storage.DataAndNewMetadata;
import de.factoryfx.data.storage.inmemory.InMemoryDataStorage;
import de.factoryfx.factory.atrribute.FactoryReferenceListAttribute;
import de.factoryfx.factory.exception.RethrowingFactoryExceptionHandler;
import de.factoryfx.factory.log.FactoryUpdateLog;
import de.factoryfx.server.Microservice;

public class FactoryReferenceListTest {

    public static class ObjectFactory extends FactoryBase<Object, Void, RootFactory> {
        ObjectFactory() {
            configLiveCycle().setCreator(() -> {
                System.out.println("creator called " + getId());
                return new Object();
            });
            configLiveCycle().setReCreator(old -> {
                System.out.println("reCreator called " + getId());
                return new Object();
            });
            configLiveCycle().setStarter(o -> System.out.println("starter called " + getId()));
            configLiveCycle().setDestroyer(o -> System.out.println("destroyer called " + getId()));
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

        RootFactory rootFactory = new RootFactory();
        rootFactory.objects.add(new ObjectFactory());
        Microservice<Void, RootFactory, Void> microService = new Microservice<>(new FactoryManager<>(new RethrowingFactoryExceptionHandler()), new InMemoryDataStorage<>(rootFactory));
        microService.start();


        {
            System.out.println("update started");
            DataAndNewMetadata<RootFactory> update1 = microService.prepareNewFactory();
            update1.root.objects.add(new ObjectFactory());

            FactoryUpdateLog<RootFactory> res1 = microService.updateCurrentFactory(update1, "root", "update1", s -> true);
            Assert.assertTrue(res1.successfullyMerged());
        }

    }

}
