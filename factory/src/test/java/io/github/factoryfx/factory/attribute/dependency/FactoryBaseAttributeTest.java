package io.github.factoryfx.factory.attribute.dependency;

import io.github.factoryfx.factory.FactoryReferenceListTest;
import io.github.factoryfx.factory.attribute.AttributeChangeListener;
import io.github.factoryfx.factory.attribute.ImmutableValueAttribute;
import io.github.factoryfx.factory.attribute.types.StringAttribute;
import io.github.factoryfx.factory.testfactories.ExampleFactoryA;
import io.github.factoryfx.factory.testfactories.ExampleLiveObjectA;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FactoryBaseAttributeTest {

    @Test
    public void test_Listener()  {
        List<ExampleFactoryA> calls= new ArrayList<>();

        FactoryAttribute<ExampleFactoryA, ExampleLiveObjectA, ExampleFactoryA> factoryAttribute = new FactoryAttribute<>();
        final AttributeChangeListener<ExampleFactoryA,FactoryAttribute<ExampleFactoryA, ExampleLiveObjectA, ExampleFactoryA>> stringAttributeChangeListener = (attribute, value) -> calls.add(value);
        factoryAttribute.internal_addListener(stringAttributeChangeListener);

        ExampleFactoryA value = new ExampleFactoryA();
        factoryAttribute.set(value);

        Assertions.assertEquals(1,calls.size());
        Assertions.assertEquals(value,calls.get(0));
    }
}