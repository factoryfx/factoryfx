package io.github.factoryfx.data.attribute;

import io.github.factoryfx.data.jackson.ObjectMapperBuilder;
import io.github.factoryfx.data.merge.testdata.ExampleDataA;
import io.github.factoryfx.data.merge.testdata.ExampleDataB;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DataDictionaryListAttributeTest {

    @Test
    public void test_json(){
        ExampleDataA data = new ExampleDataA();
        data.referenceListAttribute.add(new ExampleDataB());
        ExampleDataA copy = ObjectMapperBuilder.build().copy(data);
        Assertions.assertTrue(data.referenceListAttribute.get(0) instanceof  ExampleDataB);
    }


}