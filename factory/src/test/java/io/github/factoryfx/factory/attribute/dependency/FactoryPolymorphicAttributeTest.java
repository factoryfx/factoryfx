package io.github.factoryfx.factory.attribute.dependency;

import io.github.factoryfx.factory.FactoryTreeBuilderBasedAttributeSetup;
import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.builder.FactoryContext;
import io.github.factoryfx.factory.builder.FactoryTreeBuilder;
import io.github.factoryfx.factory.jackson.SimpleObjectMapper;
import io.github.factoryfx.factory.metadata.FactoryMetadataManager;
import io.github.factoryfx.factory.testfactories.ExampleLiveObjectA;
import io.github.factoryfx.factory.validation.ValidationError;
import io.github.factoryfx.factory.testfactories.ExampleFactoryA;
import io.github.factoryfx.factory.testfactories.poly.ErrorPrinter;
import io.github.factoryfx.factory.testfactories.poly.ErrorPrinterFactory;
import io.github.factoryfx.factory.testfactories.poly.OutPrinterFactory;
import io.github.factoryfx.factory.testfactories.poly.Printer;
import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;
import io.github.factoryfx.factory.FactoryBase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.internal.matchers.Or;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class FactoryPolymorphicAttributeTest {


    @Test
    public void test_json_inside_data(){
        PolymorphicFactoryExample factory = new PolymorphicFactoryExample();
        factory.reference.set(new ErrorPrinterFactory());
        PolymorphicFactoryExample copy = ObjectMapperBuilder.build().copy(factory);

        Assertions.assertNotNull(copy.reference.get());
    }


    public static class ErrorPrinterFactory2 extends SimpleFactoryBase<Printer,ExampleFactoryA> {
        @Override
        protected ErrorPrinter createImpl() {
            return new ErrorPrinter();
        }
    }


    @Test
    public void test_null(){
        FactoryPolymorphicAttribute<Printer> attribute = new FactoryPolymorphicAttribute<>();

        {
            attribute.set(null);
            List<ValidationError> validationErrors = attribute.internal_validate(new ExampleFactoryA(),"");
            Assertions.assertEquals(1, validationErrors.size());
        }

        {
            attribute.set(new ErrorPrinterFactory());
            List<ValidationError> validationErrors = attribute.internal_validate(new ExampleFactoryA(),"");
            Assertions.assertEquals(0, validationErrors.size());
        }
    }

    @Test
    public void test_nullable(){
        FactoryPolymorphicAttribute<Printer> attribute = new FactoryPolymorphicAttribute<Printer>().nullable();

        {
            attribute.set(null);
            List<ValidationError> validationErrors = attribute.internal_validate(new ExampleFactoryA(),"");
            Assertions.assertEquals(0, validationErrors.size());
        }

        {
            attribute.set(new ErrorPrinterFactory());
            List<ValidationError> validationErrors = attribute.internal_validate(new ExampleFactoryA(),"");
            Assertions.assertEquals(0, validationErrors.size());
        }
    }

    @Test
    public void test_nullable_with_constructor(){
        FactoryPolymorphicAttribute<Printer> attribute = new FactoryPolymorphicAttribute<Printer>().nullable();

        {
            attribute.set(null);
            List<ValidationError> validationErrors = attribute.internal_validate(new ExampleFactoryA(),"");
            Assertions.assertEquals(0, validationErrors.size());
        }

        {
            attribute.set(new ErrorPrinterFactory());
            List<ValidationError> validationErrors = attribute.internal_validate(new ExampleFactoryA(),"");
            Assertions.assertEquals(0, validationErrors.size());
        }
    }

    @Test
    public void test_internal_require_true(){
        FactoryPolymorphicAttribute<Printer> attribute = new FactoryPolymorphicAttribute<>();
        Assertions.assertTrue(attribute.internal_required());
    }

    @Test
    public void test_internal_require_false(){
        FactoryPolymorphicAttribute<Printer> attribute = new FactoryPolymorphicAttribute<Printer>().nullable();
        Assertions.assertFalse(attribute.internal_required());
    }



    @Test
    public void test_referenclass(){
        PolymorphicFactoryExample root = new PolymorphicFactoryExample();
        Assertions.assertNull(FactoryMetadataManager.getMetadata(PolymorphicFactoryExample.class).getAttributeMetadata(f->f.reference).referenceClass);//Reference class can' be determined from the generic parameter
    }
}