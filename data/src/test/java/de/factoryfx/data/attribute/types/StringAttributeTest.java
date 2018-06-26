package de.factoryfx.data.attribute.types;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.data.validation.ValidationError;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;


public class StringAttributeTest {

    @Test
    public void test_json(){
        StringAttribute attribute = new StringAttribute();
        String value = "123";
        attribute.set(value);
        StringAttribute copy = ObjectMapperBuilder.build().copy(attribute);
        System.out.println(ObjectMapperBuilder.build().writeValueAsString(copy));
        Assert.assertEquals(value,copy.get());
    }

    @Test
    public void test_empty(){
        StringAttribute attribute = new StringAttribute();

        {
            attribute.set("");
            List<ValidationError> validationErrors = attribute.internal_validate(null,"");
            Assert.assertEquals(1, validationErrors.size());
        }

        {
            attribute.set("123");
            List<ValidationError> validationErrors = attribute.internal_validate(null,"");
            Assert.assertEquals(0, validationErrors.size());
        }
    }

}