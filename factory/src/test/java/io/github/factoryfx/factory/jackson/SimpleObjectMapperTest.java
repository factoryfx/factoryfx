package io.github.factoryfx.factory.jackson;

import io.github.factoryfx.factory.testfactories.ExampleFactoryA;
import io.github.factoryfx.factory.testfactories.ExampleFactoryB;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class SimpleObjectMapperTest {
    @Test
    public void test_copy_ids() {
        ExampleFactoryA exampleListFactory = new ExampleFactoryA();
        ExampleFactoryB factoryB = new ExampleFactoryB();
        exampleListFactory.referenceAttribute.set(factoryB);
        exampleListFactory.referenceListAttribute.add(factoryB);

        ExampleFactoryA copy = ObjectMapperBuilder.build().copy(exampleListFactory);
        assertEquals(copy.referenceAttribute.get(), copy.referenceListAttribute.get(0));
    }

    @Test
    public void test_outputStyle() {
        ExampleFactoryA factoryA = new ExampleFactoryA();
        SimpleObjectMapper simpleObjectMapper = ObjectMapperBuilder.build();

        String prettyStr = simpleObjectMapper.writeValueAsString(factoryA, OutputStyle.PRETTY);
        String compactStr = simpleObjectMapper.writeValueAsString(factoryA, OutputStyle.COMPACT);
        String defaultStr = simpleObjectMapper.writeValueAsString(factoryA, OutputStyle.DEFAULT);

        Function<String, Boolean> isStringPretty = s -> s.contains("\" : ") && s.contains("\n") && !s.contains("\":") && s.contains("{\n");

        assertEquals(prettyStr, defaultStr);
        assertTrue(isStringPretty.apply(prettyStr));
        assertTrue(isStringPretty.apply(defaultStr));
        assertTrue(compactStr.length() < prettyStr.length());
        assertTrue(compactStr.length() < defaultStr.length());
        assertFalse(isStringPretty.apply(compactStr));
    }
}