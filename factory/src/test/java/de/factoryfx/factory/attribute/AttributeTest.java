package de.factoryfx.factory.attribute;

import java.util.List;
import java.util.Optional;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.attribute.util.StringAttribute;
import de.factoryfx.factory.testfactories.ExampleLiveObjectA;
import de.factoryfx.factory.validation.StringRequired;
import de.factoryfx.factory.validation.ValidationError;
import org.junit.Assert;
import org.junit.Test;

public class AttributeTest {

    public class ExampleFactoryA extends FactoryBase<ExampleLiveObjectA,de.factoryfx.factory.testfactories.ExampleFactoryA> {
        public final StringAttribute stringAttribute= new StringAttribute(new AttributeMetadata().labelText("ExampleA1")).validation(new StringRequired());

        @Override
        protected ExampleLiveObjectA createImp(Optional<ExampleLiveObjectA> previousLiveObject) {
            return null;
        }

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