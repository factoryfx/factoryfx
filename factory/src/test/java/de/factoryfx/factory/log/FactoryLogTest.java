package de.factoryfx.factory.log;

import java.util.HashSet;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import org.junit.Test;

public class FactoryLogTest {

    @Test
    public void test_json(){
        FactoryUpdateLog factoryLog = new FactoryUpdateLog(null,new HashSet<>(),null,0);
        ObjectMapperBuilder.build().copy(factoryLog);
    }

}