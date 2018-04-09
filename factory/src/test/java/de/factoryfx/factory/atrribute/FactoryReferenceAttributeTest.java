package de.factoryfx.factory.atrribute;

import de.factoryfx.data.validation.ValidationError;
import de.factoryfx.factory.testfactories.ExampleFactoryA;
import de.factoryfx.factory.testfactories.ExampleLiveObjectA;
import de.factoryfx.factory.testfactories.poly.ErrorPrinterFactory;
import de.factoryfx.factory.testfactories.poly.OutPrinterFactory;
import de.factoryfx.factory.testfactories.poly.Printer;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;


public class FactoryReferenceAttributeTest {

    @Test
    public void test_null(){
        FactoryReferenceAttribute<ExampleLiveObjectA, ExampleFactoryA> attribute = new FactoryReferenceAttribute<>();

        {
            attribute.set(null);
            List<ValidationError> validationErrors = attribute.internal_validate(new ExampleFactoryA());
            Assert.assertEquals(1, validationErrors.size());
        }

        {
            attribute.set(new ExampleFactoryA());
            List<ValidationError> validationErrors = attribute.internal_validate(new ExampleFactoryA());
            Assert.assertEquals(0, validationErrors.size());
        }
    }

    @Test
    public void test_internal_require_true(){
        FactoryReferenceAttribute<ExampleLiveObjectA, ExampleFactoryA> attribute = new FactoryReferenceAttribute<>();
        Assert.assertTrue(attribute.internal_required());
    }

    @Test
    public void test_internal_require_false(){
        FactoryReferenceAttribute<ExampleLiveObjectA, ExampleFactoryA> attribute = new FactoryReferenceAttribute<>(ExampleFactoryA.class).nullable();
        Assert.assertFalse(attribute.internal_required());
    }

    @Test
    public void test_nullable(){
        FactoryReferenceAttribute<ExampleLiveObjectA, ExampleFactoryA> attribute = new FactoryReferenceAttribute<>(ExampleFactoryA.class).nullable();


        {
            attribute.set(null);
            List<ValidationError> validationErrors = attribute.internal_validate(new ExampleFactoryA());
            Assert.assertEquals(0, validationErrors.size());
        }

        {
            attribute.set(new ExampleFactoryA());
            List<ValidationError> validationErrors = attribute.internal_validate(new ExampleFactoryA());
            Assert.assertEquals(0, validationErrors.size());
        }
    }
}