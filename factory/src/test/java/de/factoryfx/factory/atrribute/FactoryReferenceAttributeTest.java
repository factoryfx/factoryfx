package de.factoryfx.factory.atrribute;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.data.validation.ValidationError;
import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.testfactories.ExampleFactoryA;
import de.factoryfx.factory.testfactories.ExampleLiveObjectA;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;


public class FactoryReferenceAttributeTest {

    @Test
    public void test_null(){
        FactoryReferenceAttribute<ExampleLiveObjectA, ExampleFactoryA> attribute = new FactoryReferenceAttribute<>(ExampleFactoryA.class);

        {
            attribute.set(null);
            List<ValidationError> validationErrors = attribute.internal_validate(new ExampleFactoryA(),"");
            Assertions.assertEquals(1, validationErrors.size());
        }

        {
            attribute.set(new ExampleFactoryA());
            List<ValidationError> validationErrors = attribute.internal_validate(new ExampleFactoryA(),"");
            Assertions.assertEquals(0, validationErrors.size());
        }
    }

    @Test
    public void test_internal_require_true(){
        FactoryReferenceAttribute<ExampleLiveObjectA, ExampleFactoryA> attribute = new FactoryReferenceAttribute<>(ExampleFactoryA.class);
        Assertions.assertTrue(attribute.internal_required());
    }

    @Test
    public void test_internal_require_false(){
        FactoryReferenceAttribute<ExampleLiveObjectA, ExampleFactoryA> attribute = new FactoryReferenceAttribute<>(ExampleFactoryA.class).nullable();
        Assertions.assertFalse(attribute.internal_required());
    }



    @Test
    public void test_nullable(){
        FactoryReferenceAttribute<ExampleLiveObjectA, ExampleFactoryA> attribute = new FactoryReferenceAttribute<>(ExampleFactoryA.class).nullable();


        {
            attribute.set(null);
            List<ValidationError> validationErrors = attribute.internal_validate(new ExampleFactoryA(),"");
            Assertions.assertEquals(0, validationErrors.size());
        }

        {
            attribute.set(new ExampleFactoryA());
            List<ValidationError> validationErrors = attribute.internal_validate(new ExampleFactoryA(),"");
            Assertions.assertEquals(0, validationErrors.size());
        }
    }

    public static class CreateExampleFactory extends SimpleFactoryBase<Void,Void,CreateExampleFactory>{
        @SuppressWarnings("unchecked")
        FactoryReferenceAttribute<ExampleLiveObjectA, ExampleFactoryA> attribute = FactoryReferenceAttribute.create(new FactoryReferenceAttribute<>(ExampleFactoryA.class).nullable());
        @Override
        public Void createImpl() {
            return null;
        }
    }

    @Test
    public void test_create_json(){
        CreateExampleFactory factory = new CreateExampleFactory();
        factory.attribute.set(new ExampleFactoryA());
        ObjectMapperBuilder.build().copy(factory);
    }

    @Test
    public void test_json(){
        ExampleFactoryA factory = new ExampleFactoryA();
        ObjectMapperBuilder.build().copy(factory);
    }
}