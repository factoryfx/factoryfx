package io.github.factoryfx.factory.attribute.types;

import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;
import io.github.factoryfx.factory.testfactories.ExampleFactoryA;
import io.github.factoryfx.factory.testfactories.ExampleFactoryB;
import io.github.factoryfx.factory.validation.ValidationError;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class StringAttributeTest {

    @Test
    public void test_json(){
        StringAttribute attribute = new StringAttribute();
        String value = "123";
        attribute.set(value);
        StringAttribute copy = ObjectMapperBuilder.build().copy(attribute);
        System.out.println(ObjectMapperBuilder.build().writeValueAsString(copy));
        Assertions.assertEquals(value,copy.get());
    }

    @Test
    public void test_empty(){
        StringAttribute attribute = new StringAttribute();

        {
            attribute.set("");
            List<ValidationError> validationErrors = attribute.internal_validate(null,"");
            Assertions.assertEquals(1, validationErrors.size());
        }

        {
            attribute.set("123");
            List<ValidationError> validationErrors = attribute.internal_validate(null,"");
            Assertions.assertEquals(0, validationErrors.size());
        }
    }

    @Test
    public void test_reset()  {
        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
        exampleFactoryA.stringAttribute.set("1111");
        exampleFactoryA.internal().finalise();

        exampleFactoryA.referenceAttribute.set(new ExampleFactoryB());
        exampleFactoryA.internal().resetModificationFlat();
        assertEquals("1111",exampleFactoryA.stringAttribute.get());
    }

    @Test
    public void test_reset_setMultiple()  {
        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
        exampleFactoryA.stringAttribute.set("1111");
        exampleFactoryA.internal().finalise();

        exampleFactoryA.stringAttribute.set("2222");
        exampleFactoryA.stringAttribute.set("3333");
        exampleFactoryA.stringAttribute.set("4444");

        exampleFactoryA.internal().resetModificationFlat();
        assertEquals("1111",exampleFactoryA.stringAttribute.get());
    }

    @Test
    public void test_reset_null()  {
        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
        exampleFactoryA.internal().finalise();

        exampleFactoryA.stringAttribute.set("22222");
        exampleFactoryA.internal().resetModificationFlat();
        assertEquals(null,exampleFactoryA.stringAttribute.get());
    }

    @Test
    public void test_reset_setNull()  {
        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
        exampleFactoryA.stringAttribute.set("1111");
        exampleFactoryA.internal().finalise();

        exampleFactoryA.stringAttribute.set(null);
        exampleFactoryA.internal().resetModificationFlat();
        assertEquals("1111",exampleFactoryA.stringAttribute.get());
    }

}