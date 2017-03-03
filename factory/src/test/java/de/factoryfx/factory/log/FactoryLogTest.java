package de.factoryfx.factory.log;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import org.junit.Test;

public class FactoryLogTest {

    @Test
    public void test_json(){
        FactoryLog factoryLog = new FactoryLog(null,null);
        ObjectMapperBuilder.build().copy(factoryLog);
    }

}