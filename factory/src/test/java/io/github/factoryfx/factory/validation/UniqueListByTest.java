package io.github.factoryfx.factory.validation;

import io.github.factoryfx.factory.attribute.dependency.FactoryListAttribute;
import io.github.factoryfx.factory.testfactories.ExampleFactoryA;
import io.github.factoryfx.factory.testfactories.ExampleLiveObjectA;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class UniqueListByTest {

    @Test
    public final void test_happycase() {
        FactoryListAttribute< ExampleLiveObjectA, ExampleFactoryA> factoryListAttribute = new FactoryListAttribute<>();
        factoryListAttribute.validation(new UniqueListBy<>(exampleFactoryA -> exampleFactoryA.stringAttribute.get()));

        ExampleFactoryA value1 = new ExampleFactoryA();
        value1.stringAttribute.set("value1");
        ExampleFactoryA value2 = new ExampleFactoryA();
        value2.stringAttribute.set("value2");
        factoryListAttribute.add(value1);
        factoryListAttribute.add(value2);
        {
            List<ValidationError> errors = factoryListAttribute.internal_validate(null, "blub");
            assertEquals(errors.size(), 0);
        }
    }

    @Test
    public final void test_error(){
        FactoryListAttribute<ExampleLiveObjectA,ExampleFactoryA> factoryListAttribute = new FactoryListAttribute<>();
        factoryListAttribute.validation(new UniqueListBy<>(exampleFactoryA -> exampleFactoryA.stringAttribute.get()));

        ExampleFactoryA value1 = new ExampleFactoryA();
        value1.stringAttribute.set("value1");
        ExampleFactoryA value2 = new ExampleFactoryA();
        value2.stringAttribute.set("value1");
        factoryListAttribute.add(value1);
        factoryListAttribute.add(value2);

        {
            List<ValidationError> errors = factoryListAttribute.internal_validate(null, "blub");
            assertEquals(errors.size(), 1);
        }
    }

}