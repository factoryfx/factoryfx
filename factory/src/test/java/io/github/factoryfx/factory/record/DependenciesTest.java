package io.github.factoryfx.factory.record;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class DependenciesTest {
    record TestExample(String stringAttribute) implements Dependencies<RecordExampleA.Dep>{

    }

    @Test
    public void copy(){
        TestExample testExample = new TestExample("test");
        Dependencies<RecordExampleA.Dep> copy = testExample.copy();
        Assertions.assertEquals(testExample.stringAttribute,((TestExample)copy).stringAttribute);

    }

}