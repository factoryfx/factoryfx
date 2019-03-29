package io.github.factoryfx.factory.log;

import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;
import io.github.factoryfx.factory.testfactories.ExampleFactoryA;
import io.github.factoryfx.factory.testfactories.ExampleFactoryB;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class FactoryLogEntryTreeItemTest {

    @Test
    public void test_json(){
        FactoryLogEntry factoryLogEntry = new FactoryLogEntry(new ExampleFactoryA());
        FactoryLogEntryTreeItem root = new FactoryLogEntryTreeItem(factoryLogEntry, List.of(new FactoryLogEntryTreeItem((new FactoryLogEntry(new ExampleFactoryB())),new ArrayList<>())));
        ObjectMapperBuilder.build().copy(root);
        System.out.println(ObjectMapperBuilder.build().writeValueAsString(root));
    }

    @Test
    public void test_log(){
        FactoryLogEntry factoryLogEntry = new FactoryLogEntry(new ExampleFactoryA());
        factoryLogEntry.logCreate(123);
        FactoryLogEntryTreeItem root = new FactoryLogEntryTreeItem(factoryLogEntry, List.of(new FactoryLogEntryTreeItem((new FactoryLogEntry(new ExampleFactoryB())),new ArrayList<>())));
        FactoryLogEntryTreeItem copy = ObjectMapperBuilder.build().copy(root);
        System.out.println(ObjectMapperBuilder.build().writeValueAsString(root));
        Assertions.assertEquals(123,copy.log.getEvents().get(0).durationNs);
    }
}