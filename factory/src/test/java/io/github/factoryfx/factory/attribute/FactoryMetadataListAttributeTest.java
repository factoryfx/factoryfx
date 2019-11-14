package io.github.factoryfx.factory.attribute;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;
import io.github.factoryfx.factory.merge.testdata.ExampleDataA;
import io.github.factoryfx.factory.merge.testdata.ExampleDataB;

public class FactoryMetadataListAttributeTest {

    @Test
    public void test_json(){
        ExampleDataA data = new ExampleDataA();
        data.referenceListAttribute.add(new ExampleDataB());
        ExampleDataA copy = ObjectMapperBuilder.build().copy(data);
        Assertions.assertNotNull(data.referenceListAttribute.get(0));
        Assertions.assertNotNull(copy.referenceListAttribute.get(0));
    }


}