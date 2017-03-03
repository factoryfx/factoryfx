package de.factoryfx.factory.log;

import java.util.ArrayList;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import org.junit.Test;

public class FactoryLogTest {

    @Test
    public void test_json(){
        FactoryLog factoryLog = new FactoryLog(null,new ArrayList<>(),null,0);
        ObjectMapperBuilder.build().copy(factoryLog);
    }

}