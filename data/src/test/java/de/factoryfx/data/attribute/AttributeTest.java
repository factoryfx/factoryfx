package de.factoryfx.data.attribute;

import java.util.List;

import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.data.merge.testfactories.ExampleFactoryA;
import de.factoryfx.data.validation.StringRequired;
import de.factoryfx.data.validation.ValidationError;
import org.junit.Assert;
import org.junit.Test;

public class AttributeTest {
    public class ValidationExampleFactory extends Data {
        public final StringAttribute stringAttribute= new StringAttribute(new AttributeMetadata().labelText("ExampleA1")).validation(new StringRequired());
    }

    @Test
    public void test_validation(){
        ValidationExampleFactory validationExampleFactory = new ValidationExampleFactory();
        validationExampleFactory.stringAttribute.set("");
        List<ValidationError> validationErrors = validationExampleFactory.internal().validateFlat();
        Assert.assertEquals(1, validationErrors.size());

        validationExampleFactory.stringAttribute.set("ssfdfdsdf");
        validationErrors = validationExampleFactory.internal().validateFlat();
        Assert.assertEquals(0, validationErrors.size());
    }

}