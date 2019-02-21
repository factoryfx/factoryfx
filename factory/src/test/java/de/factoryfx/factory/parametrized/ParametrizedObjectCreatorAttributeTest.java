package de.factoryfx.factory.parametrized;

import de.factoryfx.data.validation.ValidationError;
import de.factoryfx.factory.testfactories.ExampleFactoryA;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class ParametrizedObjectCreatorAttributeTest {
    @Test
    public void test_null(){
        ParametrizedObjectCreatorAttribute<ParametrizedObjectCreatorFactoryTest.ShortLivedParameter,ParametrizedObjectCreatorFactoryTest.ShortLivedLiveObject,ParametrizedObjectCreatorFactoryTest.ShortLivedLiveObjectCreatorFactory> attribute = new ParametrizedObjectCreatorAttribute<>(ParametrizedObjectCreatorFactoryTest.ShortLivedLiveObjectCreatorFactory.class);

        {
            attribute.set(null);
            List<ValidationError> validationErrors = attribute.internal_validate(new ExampleFactoryA(),"");
            Assertions.assertEquals(1, validationErrors.size());
        }

        {
            attribute.set(new ParametrizedObjectCreatorFactoryTest.ShortLivedLiveObjectCreatorFactory());
            List<ValidationError> validationErrors = attribute.internal_validate(new ExampleFactoryA(),"");
            Assertions.assertEquals(0, validationErrors.size());
        }
    }

    @Test
    public void test_nullable(){
        ParametrizedObjectCreatorAttribute<ParametrizedObjectCreatorFactoryTest.ShortLivedParameter,ParametrizedObjectCreatorFactoryTest.ShortLivedLiveObject,ParametrizedObjectCreatorFactoryTest.ShortLivedLiveObjectCreatorFactory> attribute = new ParametrizedObjectCreatorAttribute<>(ParametrizedObjectCreatorFactoryTest.ShortLivedLiveObjectCreatorFactory.class).nullable();

        {
            attribute.set(null);
            List<ValidationError> validationErrors = attribute.internal_validate(new ExampleFactoryA(),"");
            Assertions.assertEquals(0, validationErrors.size());
        }

        {
            attribute.set(new ParametrizedObjectCreatorFactoryTest.ShortLivedLiveObjectCreatorFactory());
            List<ValidationError> validationErrors = attribute.internal_validate(new ExampleFactoryA(),"");
            Assertions.assertEquals(0, validationErrors.size());
        }
    }

    @Test
    public void test_internal_require_true(){
        ParametrizedObjectCreatorAttribute<ParametrizedObjectCreatorFactoryTest.ShortLivedParameter,ParametrizedObjectCreatorFactoryTest.ShortLivedLiveObject,ParametrizedObjectCreatorFactoryTest.ShortLivedLiveObjectCreatorFactory> attribute = new ParametrizedObjectCreatorAttribute<>(ParametrizedObjectCreatorFactoryTest.ShortLivedLiveObjectCreatorFactory.class);
        Assertions.assertTrue(attribute.internal_required());
    }

    @Test
    public void test_internal_require_false(){
        ParametrizedObjectCreatorAttribute<ParametrizedObjectCreatorFactoryTest.ShortLivedParameter,ParametrizedObjectCreatorFactoryTest.ShortLivedLiveObject,ParametrizedObjectCreatorFactoryTest.ShortLivedLiveObjectCreatorFactory> attribute = new ParametrizedObjectCreatorAttribute<>(ParametrizedObjectCreatorFactoryTest.ShortLivedLiveObjectCreatorFactory.class).nullable();
        Assertions.assertFalse(attribute.internal_required());
    }

}