package io.github.factoryfx.factory.validation;

import io.github.factoryfx.factory.FactoryReferenceListTest;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttributeTest;
import io.github.factoryfx.factory.attribute.dependency.FactoryListAttribute;
import io.github.factoryfx.factory.attribute.types.StringListAttribute;
import io.github.factoryfx.factory.merge.testdata.ExampleDataA;
import io.github.factoryfx.factory.testfactories.ExampleFactoryA;
import io.github.factoryfx.factory.testfactories.ExampleLiveObjectA;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UniqueListTest {

    @Test
    public final void test_happycase() {
        FactoryListAttribute<ExampleFactoryA, ExampleLiveObjectA, ExampleFactoryA> factoryListAttribute = new FactoryListAttribute<>();
        factoryListAttribute.validation(new UniqueList<>());

        factoryListAttribute.add(new ExampleFactoryA());
        factoryListAttribute.add(new ExampleFactoryA());
        {
            List<ValidationError> errors = factoryListAttribute.internal_validate(null, "blub");
            assertEquals(errors.size(), 0);
        }
    }

    @Test
    public final void test_error(){
        FactoryListAttribute<ExampleFactoryA, ExampleLiveObjectA,ExampleFactoryA> factoryListAttribute = new FactoryListAttribute<>();
        factoryListAttribute.validation(new UniqueList<>());

        ExampleFactoryA value = new ExampleFactoryA();
        factoryListAttribute.add(value);
        factoryListAttribute.add(value);

        {
            List<ValidationError> errors = factoryListAttribute.internal_validate(null, "blub");
            assertEquals(errors.size(), 1);
        }
    }
}