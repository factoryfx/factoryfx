package io.github.factoryfx.factory.jackson;

import io.github.factoryfx.factory.attribute.types.StringListAttributeTest;
import io.github.factoryfx.factory.testfactories.ExampleFactoryA;
import io.github.factoryfx.factory.testfactories.ExampleFactoryB;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SimpleObjectMapperTest {
    @Test
    public void test_copy_ids() {
        ExampleFactoryA exampleListFactory = new ExampleFactoryA();
        ExampleFactoryB factoryB = new ExampleFactoryB();
        exampleListFactory.referenceAttribute.set(factoryB);
        exampleListFactory.referenceListAttribute.add(factoryB);

        ExampleFactoryA copy = ObjectMapperBuilder.build().copy(exampleListFactory);
        Assertions.assertEquals(copy.referenceAttribute.get(),copy.referenceListAttribute.get(0));
    }
}