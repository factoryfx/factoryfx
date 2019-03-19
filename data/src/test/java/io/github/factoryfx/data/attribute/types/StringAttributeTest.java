package io.github.factoryfx.data.attribute.types;

import io.github.factoryfx.data.jackson.ObjectMapperBuilder;
import io.github.factoryfx.data.validation.ValidationError;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;


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

}