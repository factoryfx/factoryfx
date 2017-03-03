package de.factoryfx.factory.log;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.factory.testfactories.ExampleFactoryA;
import org.junit.Test;

public class FactoryLogEntryTest {
    @Test
    public void test_json(){
        FactoryLogEntry factoryLogEntry = new FactoryLogEntry(new ExampleFactoryA());
        ObjectMapperBuilder.build().copy(factoryLogEntry);
    }
}