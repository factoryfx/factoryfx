package io.github.factoryfx.factory.record;

import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class RecordDeserializerTest {

    @Test
    public void test_json_value_only(){
        RecordFactory<RecordExampleB,RecordExampleB.Dep,RootFactory> factoryB = new RecordFactory<>(new RecordExampleB.Dep("testB2"));
        RecordFactory<RecordExampleB, RecordExampleB.Dep, RootFactory> copy = ObjectMapperBuilder.build().copy(factoryB);
        Assertions.assertEquals("testB2",copy.dep().stringAttribute());
    }

    @Test
    public void test_json_value_only_null(){
        RecordFactory<RecordExampleB,RecordExampleB.Dep,RootFactory> factoryB = new RecordFactory<>(new RecordExampleB.Dep(null));
        RecordFactory<RecordExampleB, RecordExampleB.Dep, RootFactory> copy = ObjectMapperBuilder.build().copy(factoryB);
        Assertions.assertNull(copy.dep().stringAttribute());
    }

    @Test
    public void test_json_nested(){
        RecordFactory<RecordExampleB,RecordExampleB.Dep,RootFactory> factoryB = new RecordFactory<>(new RecordExampleB.Dep("testB2"));
        RecordFactory<RecordExampleA,RecordExampleA.Dep,RootFactory> factoryA = new RecordFactory<>(new RecordExampleA.Dep(new Dependency<>(factoryB),new DependencyList<>(List.of(factoryB))));

        System.out.println(ObjectMapperBuilder.build().writeValueAsString(factoryA));

        RecordFactory<RecordExampleA, RecordExampleA.Dep, RootFactory> copy = ObjectMapperBuilder.build().copy(factoryA);
        Assertions.assertNotNull(copy.dep().exampleB().dep());
    }

}