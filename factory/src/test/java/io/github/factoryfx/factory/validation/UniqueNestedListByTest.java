package io.github.factoryfx.factory.validation;

import io.github.factoryfx.factory.attribute.dependency.FactoryListAttribute;
import io.github.factoryfx.factory.testfactories.ExampleFactoryA;
import io.github.factoryfx.factory.testfactories.ExampleFactoryB;
import io.github.factoryfx.factory.testfactories.ExampleLiveObjectA;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UniqueNestedListByTest {



    @Test
    public final void test_happycase() {
        FactoryListAttribute<ExampleLiveObjectA, ExampleFactoryA> factoryListAttribute = new FactoryListAttribute<>();
        factoryListAttribute.validation(new UniqueNestedListBy<>(exampleFactoryA -> exampleFactoryA.referenceListAttribute.get(), exampleFactoryB -> exampleFactoryB.stringAttribute.get()));

        ExampleFactoryA value1 = new ExampleFactoryA();
        value1.stringAttribute.set("value1");

        ExampleFactoryB b1 = new ExampleFactoryB();
        b1.stringAttribute.set("bValue1");
        value1.referenceListAttribute.add(b1);

        ExampleFactoryB b2 = new ExampleFactoryB();
        b2.stringAttribute.set("bValue2");
        value1.referenceListAttribute.add(b2);

        ExampleFactoryA value2 = new ExampleFactoryA();
        value2.stringAttribute.set("value2");

        ExampleFactoryB b3 = new ExampleFactoryB();
        b3.stringAttribute.set("bValue3");
        value2.referenceListAttribute.add(b3);

        ExampleFactoryB b4 = new ExampleFactoryB();
        b4.stringAttribute.set("bValue4");
        value2.referenceListAttribute.add(b4);

        factoryListAttribute.add(value1);
        factoryListAttribute.add(value2);
        {
            List<ValidationError> errors = factoryListAttribute.internal_validate(null, "blub");
            assertEquals(errors.size(), 0);
        }
    }

    @Test
    public final void test_error(){
        FactoryListAttribute<ExampleLiveObjectA, ExampleFactoryA> factoryListAttribute = new FactoryListAttribute<>();
        factoryListAttribute.validation(new UniqueNestedListBy<>(exampleFactoryA -> exampleFactoryA.referenceListAttribute.get(), exampleFactoryB -> exampleFactoryB.stringAttribute.get()));

        ExampleFactoryA value1 = new ExampleFactoryA();
        value1.stringAttribute.set("value1");

        ExampleFactoryB b1 = new ExampleFactoryB();
        b1.stringAttribute.set("bValue1");
        value1.referenceListAttribute.add(b1);

        ExampleFactoryB b2 = new ExampleFactoryB();
        b2.stringAttribute.set("bValue2");
        value1.referenceListAttribute.add(b2);

        ExampleFactoryA value2 = new ExampleFactoryA();
        value2.stringAttribute.set("value2");

        ExampleFactoryB b3 = new ExampleFactoryB();
        b3.stringAttribute.set("bValue2");
        value2.referenceListAttribute.add(b3);

        ExampleFactoryB b4 = new ExampleFactoryB();
        b4.stringAttribute.set("bValue4");
        value2.referenceListAttribute.add(b4);

        factoryListAttribute.add(value1);
        factoryListAttribute.add(value2);

        {
            List<ValidationError> errors = factoryListAttribute.internal_validate(null, "blub");
            assertEquals(errors.size(), 1);
        }
    }

}