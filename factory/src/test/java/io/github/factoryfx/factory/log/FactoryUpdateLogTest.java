package io.github.factoryfx.factory.log;

import io.github.factoryfx.data.jackson.ObjectMapperBuilder;
import io.github.factoryfx.data.merge.DataMerger;
import io.github.factoryfx.factory.testfactories.ExampleFactoryA;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;

public class FactoryUpdateLogTest {

    @Test
    public void test_json(){
        DataMerger<ExampleFactoryA> dataMerger = new DataMerger<>(new ExampleFactoryA(),new ExampleFactoryA(),new ExampleFactoryA());

        FactoryUpdateLog<ExampleFactoryA> factoryUpdateLog = new FactoryUpdateLog<>(
                new FactoryLogEntryTreeItem(
                        new FactoryLogEntry(new ExampleFactoryA()),new ArrayList<>()),new HashSet<>(),
                        dataMerger.mergeIntoCurrent(p->true),0,null);
        ObjectMapperBuilder.build().copy(factoryUpdateLog);
    }

}