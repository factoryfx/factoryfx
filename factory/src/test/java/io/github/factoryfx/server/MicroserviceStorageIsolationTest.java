package io.github.factoryfx.server;

import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.types.StringAttribute;
import io.github.factoryfx.factory.builder.FactoryTreeBuilder;
import io.github.factoryfx.factory.storage.StoredDataMetadata;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * guards the removed defensive copy on save: storages must not see later mutations of the live factory tree
 */
public class MicroserviceStorageIsolationTest {

    public static class ExampleFactory extends SimpleFactoryBase<Void, ExampleFactory> {
        public final StringAttribute stringAttribute = new StringAttribute().nullable();

        @Override
        protected Void createImpl() {
            return null;
        }
    }

    @Test
    public void test_storedHistoryUnaffectedByLaterLiveTreeMutations() {
        FactoryTreeBuilder<Void, ExampleFactory> builder = new FactoryTreeBuilder<>(ExampleFactory.class, ctx -> {
            ExampleFactory factory = new ExampleFactory();
            factory.stringAttribute.set("initial");
            return factory;
        });
        Microservice<Void, ExampleFactory> microservice = builder.microservice().build();
        microservice.start();

        microservice.update((root, idToFactory) -> root.stringAttribute.set("A"));
        microservice.update((root, idToFactory) -> root.stringAttribute.set("B"));

        List<StoredDataMetadata> history = new ArrayList<>(microservice.getHistoryFactoryList(true));
        history.sort(Comparator.comparing(metadata -> metadata.creationTime));
        Assertions.assertEquals(3, history.size());
        //with an aliasing bug the "A" history entry would show the later mutation "B"
        Assertions.assertEquals("A", microservice.getHistoryFactory(history.get(1).id).stringAttribute.get());
        Assertions.assertEquals("B", microservice.getHistoryFactory(history.get(2).id).stringAttribute.get());
        microservice.stop();
    }
}
