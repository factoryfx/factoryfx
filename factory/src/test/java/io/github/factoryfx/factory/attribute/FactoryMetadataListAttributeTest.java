package io.github.factoryfx.factory.attribute;

import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;
import io.github.factoryfx.factory.merge.testdata.ExampleDataA;
import io.github.factoryfx.factory.merge.testdata.ExampleDataB;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FactoryMetadataListAttributeTest {

    @Test
    public void test_json(){
        ExampleDataA data = new ExampleDataA();
        data.referenceListAttribute.add(new ExampleDataB());
        ExampleDataA copy = ObjectMapperBuilder.build().copy(data);
        Assertions.assertTrue(data.referenceListAttribute.get(0) instanceof  ExampleDataB);
    }


}