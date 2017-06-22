package de.factoryfx.factory.log;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.factory.testfactories.ExampleFactoryA;
import de.factoryfx.factory.testfactories.ExampleFactoryB;
import org.junit.Test;

public class FactoryLogEntryTest {
    @Test
    public void test_json(){
        FactoryLogEntry factoryLogEntry = new FactoryLogEntry(new ExampleFactoryA());
        factoryLogEntry.children.add(new FactoryLogEntry(new ExampleFactoryB()));
        ObjectMapperBuilder.build().copy(factoryLogEntry);
        System.out.println(ObjectMapperBuilder.build().writeValueAsString(factoryLogEntry));
    }
}