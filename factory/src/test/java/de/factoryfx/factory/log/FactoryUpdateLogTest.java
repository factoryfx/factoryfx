package de.factoryfx.factory.log;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.data.merge.DataMerger;
import de.factoryfx.factory.testfactories.ExampleFactoryA;
import org.junit.Test;

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