package de.factoryfx.data.attribute;

import java.util.List;

import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.data.merge.testfactories.IdData;
import de.factoryfx.data.validation.StringRequired;
import de.factoryfx.data.validation.ValidationError;
import org.junit.Assert;
import org.junit.Test;

public class AttributeTest {

    public class ExampleFactoryA extends IdData {
        public final StringAttribute stringAttribute= new StringAttribute(new AttributeMetadata().labelText("ExampleA1")).validation(new StringRequired());
    }


    @Test
    public void test_validation(){
        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
        exampleFactoryA.stringAttribute.set("");
        List<ValidationError> validationErrors = exampleFactoryA.validateFlat();
        Assert.assertEquals(1, validationErrors.size());

        exampleFactoryA.stringAttribute.set("ssfdfdsdf");
        validationErrors = exampleFactoryA.validateFlat();
        Assert.assertEquals(0, validationErrors.size());
    }

}