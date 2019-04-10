package io.github.factoryfx.factory.attribute.dependency;

import io.github.factoryfx.factory.validation.ValidationError;
import io.github.factoryfx.factory.PolymorphicFactoryBase;
import io.github.factoryfx.factory.testfactories.ExampleFactoryA;
import io.github.factoryfx.factory.testfactories.poly.ErrorPrinter;
import io.github.factoryfx.factory.testfactories.poly.ErrorPrinterFactory;
import io.github.factoryfx.factory.testfactories.poly.OutPrinterFactory;
import io.github.factoryfx.factory.testfactories.poly.Printer;
import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;
import io.github.factoryfx.factory.FactoryBase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class FactoryPolymorphicAttributeTest {


    @Test
    public void test_json_inside_data(){
        ExamplePolymorphicReferenceAttributeFactory factory = new ExamplePolymorphicReferenceAttributeFactory();
        factory.attribute.set(new ErrorPrinterFactory());
        ExamplePolymorphicReferenceAttributeFactory copy = ObjectMapperBuilder.build().copy(factory);

        Assertions.assertNotNull(copy.attribute.get());
    }

    public static class ExamplePolymorphicReferenceAttributeFactory extends FactoryBase<Printer,ExampleFactoryA>{
        public final FactoryPolymorphicAttribute<ExampleFactoryA,Printer> attribute = new FactoryPolymorphicAttribute<>(Printer.class);
    }


    @Test
    public void test_select(){
        PolymorphicFactoryExample polymorphicFactoryExample = new PolymorphicFactoryExample();
        polymorphicFactoryExample = polymorphicFactoryExample.internal().addBackReferences();
        ErrorPrinterFactory errorPrinterFactory = new ErrorPrinterFactory();
        polymorphicFactoryExample.reference.set(errorPrinterFactory);
        Assertions.assertEquals(errorPrinterFactory,new ArrayList<>(polymorphicFactoryExample.reference.internal_possibleValues()).get(0));
    }

    @Test
    public void test_new_value(){
        PolymorphicFactoryExample polymorphicFactoryExample = new PolymorphicFactoryExample();
        polymorphicFactoryExample = polymorphicFactoryExample.internal().addBackReferences();

        List<FactoryBase<? extends Printer, ExampleFactoryA>> factoryBases = polymorphicFactoryExample.reference.internal_createNewPossibleValues();
        Assertions.assertEquals(ErrorPrinterFactory.class,new ArrayList<>(factoryBases).get(0).getClass());
        Assertions.assertEquals(OutPrinterFactory.class,new ArrayList<>(polymorphicFactoryExample.reference.internal_createNewPossibleValues()).get(1).getClass());
    }

    @Test
    public void test_setupUnsafe_validation(){
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new FactoryPolymorphicAttribute<ExampleFactoryA,Printer>().setupUnsafe(Printer.class,String.class);
        });
    }

    @Test
    public void test_setupUnsafe_validation_happy_case(){
        new FactoryPolymorphicAttribute<ExampleFactoryA,Printer>().setupUnsafe(Printer.class,ErrorPrinterFactory.class);
    }

    @Test
    public void test_generatorInfo_safe(){
        FactoryPolymorphicAttribute<ExampleFactoryA,Printer> attribute = new FactoryPolymorphicAttribute<ExampleFactoryA,Printer>().setup(Printer.class, ErrorPrinterFactory.class, OutPrinterFactory.class);
        Assertions.assertEquals(ErrorPrinterFactory.class,attribute.internal_possibleFactoriesClasses().get(0));
        Assertions.assertEquals(OutPrinterFactory.class,attribute.internal_possibleFactoriesClasses().get(1));
    }

    @Test
    public void test_generatorInfo_unsafe(){
        FactoryPolymorphicAttribute<ExampleFactoryA,Printer> attribute = new FactoryPolymorphicAttribute<ExampleFactoryA,Printer>().setupUnsafe(Printer.class, ErrorPrinterFactory.class, OutPrinterFactory.class);
        Assertions.assertEquals(ErrorPrinterFactory.class,attribute.internal_possibleFactoriesClasses().get(0));
        Assertions.assertEquals(OutPrinterFactory.class,attribute.internal_possibleFactoriesClasses().get(1));
    }


    @Test
    public void test_generatorInfo_constructor(){
        FactoryPolymorphicAttribute<ExampleFactoryA,Printer> attribute = new FactoryPolymorphicAttribute<>(Printer.class, ErrorPrinterFactory.class, OutPrinterFactory.class);
        Assertions.assertEquals(ErrorPrinterFactory.class,attribute.internal_possibleFactoriesClasses().get(0));
        Assertions.assertEquals(OutPrinterFactory.class,attribute.internal_possibleFactoriesClasses().get(1));
    }

    public static class ErrorPrinterFactory2 extends PolymorphicFactoryBase<Printer,ExampleFactoryA> {
        @Override
        public ErrorPrinter createImpl() {
            return new ErrorPrinter();
        }
    }

    @Test
    public void test_set(){
        PolymorphicFactoryExample polymorphicFactoryExample = new PolymorphicFactoryExample();
        polymorphicFactoryExample = polymorphicFactoryExample.internal().addBackReferences();
        ErrorPrinterFactory2 errorPrinterFactory = new ErrorPrinterFactory2();
        polymorphicFactoryExample.reference.set(errorPrinterFactory);
        Assertions.assertEquals(errorPrinterFactory,new ArrayList<>(polymorphicFactoryExample.reference.internal_possibleValues()).get(0));
    }

    @Test
    public void test_null(){
        FactoryPolymorphicAttribute<ExampleFactoryA,Printer> attribute = new FactoryPolymorphicAttribute<ExampleFactoryA,Printer>().setup(Printer.class, ErrorPrinterFactory.class, OutPrinterFactory.class);

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
        FactoryPolymorphicAttribute<ExampleFactoryA,Printer> attribute = new FactoryPolymorphicAttribute<ExampleFactoryA,Printer>().setup(Printer.class, ErrorPrinterFactory.class, OutPrinterFactory.class).nullable();

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
        FactoryPolymorphicAttribute<ExampleFactoryA,Printer> attribute = new FactoryPolymorphicAttribute<ExampleFactoryA,Printer>().setup(Printer.class, ErrorPrinterFactory.class, OutPrinterFactory.class);
        Assertions.assertTrue(attribute.internal_required());
    }

    @Test
    public void test_internal_require_false(){
        FactoryPolymorphicAttribute<ExampleFactoryA,Printer> attribute = new FactoryPolymorphicAttribute<ExampleFactoryA,Printer>().setup(Printer.class, ErrorPrinterFactory.class, OutPrinterFactory.class).nullable();
        Assertions.assertFalse(attribute.internal_required());
    }
}