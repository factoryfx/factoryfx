package de.factoryfx.factory.log;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import org.junit.Test;

public class FactoryLogEntryItemTest {
    @Test
    public void test_json(){
        FactoryLogEntryEvent factoryLogEntryItem = new FactoryLogEntryEvent(null,8);
        ObjectMapperBuilder.build().copy(factoryLogEntryItem);
    }
}