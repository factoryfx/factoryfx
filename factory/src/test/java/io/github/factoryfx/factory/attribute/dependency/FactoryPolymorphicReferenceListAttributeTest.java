package io.github.factoryfx.factory.attribute.dependency;

import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;
import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.testfactories.ExampleFactoryA;
import io.github.factoryfx.factory.testfactories.poly.ErrorPrinterFactory;
import io.github.factoryfx.factory.testfactories.poly.OutPrinterFactory;
import io.github.factoryfx.factory.testfactories.poly.Printer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class FactoryPolymorphicReferenceListAttributeTest {
    @Test
    public void test_select(){
        PolymorphicFactoryExample polymorphicFactoryExample = new PolymorphicFactoryExample();
        ErrorPrinterFactory errorPrinterFactory = new ErrorPrinterFactory();
        polymorphicFactoryExample.referenceList.add(errorPrinterFactory);
        polymorphicFactoryExample.internal().addBackReferences();

        Assertions.assertEquals(polymorphicFactoryExample.referenceList.get(0),new ArrayList<>(polymorphicFactoryExample.reference.internal_possibleValues()).get(0));
    }

    @Test
    public void test_new_value(){
        PolymorphicFactoryExample polymorphicFactoryExample = new PolymorphicFactoryExample();
        polymorphicFactoryExample.internal().addBackReferences();

        List<FactoryBase<? extends Printer, ExampleFactoryA>> factoryBases = polymorphicFactoryExample.referenceList.internal_createNewPossibleValues();
        Assertions.assertEquals(ErrorPrinterFactory.class,new ArrayList<>(factoryBases).get(0).getClass());
        Assertions.assertEquals(OutPrinterFactory.class,new ArrayList<>(polymorphicFactoryExample.reference.internal_createNewPossibleValues()).get(1).getClass());
    }

    @Test
    public void test_setupUnsafe_validation(){
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new FactoryPolymorphicReferenceListAttribute<ExampleFactoryA,Printer>().setupUnsafe(Printer.class, String.class);
        });
    }

    @Test
    public void test_setupUnsafe_validation_happy_case(){
        new FactoryPolymorphicReferenceListAttribute<ExampleFactoryA,Printer>().setupUnsafe(Printer.class,ErrorPrinterFactory.class);
    }

    @Test
    public void test_generatorInfo_safe(){
        FactoryPolymorphicReferenceListAttribute<ExampleFactoryA,Printer> attribute = new FactoryPolymorphicReferenceListAttribute<ExampleFactoryA,Printer>().setup(Printer.class, ErrorPrinterFactory.class, OutPrinterFactory.class);
        Assertions.assertEquals(ErrorPrinterFactory.class,attribute.internal_possibleFactoriesClasses().get(0));
        Assertions.assertEquals(OutPrinterFactory.class,attribute.internal_possibleFactoriesClasses().get(1));
    }

    @Test
    public void test_generatorInfo_unsafe(){
        FactoryPolymorphicReferenceListAttribute<ExampleFactoryA,Printer> attribute = new FactoryPolymorphicReferenceListAttribute<ExampleFactoryA,Printer>().setupUnsafe(Printer.class, ErrorPrinterFactory.class, OutPrinterFactory.class);
        Assertions.assertEquals(ErrorPrinterFactory.class,attribute.internal_possibleFactoriesClasses().get(0));
        Assertions.assertEquals(OutPrinterFactory.class,attribute.internal_possibleFactoriesClasses().get(1));
    }


    @Test
    public void test_generatorInfo_constructor(){
        FactoryPolymorphicReferenceListAttribute<ExampleFactoryA,Printer> attribute = new FactoryPolymorphicReferenceListAttribute<>(Printer.class, ErrorPrinterFactory.class, OutPrinterFactory.class);
        Assertions.assertEquals(ErrorPrinterFactory.class,attribute.internal_possibleFactoriesClasses().get(0));
        Assertions.assertEquals(OutPrinterFactory.class,attribute.internal_possibleFactoriesClasses().get(1));
    }

    @Test
    public void test_set(){
        PolymorphicFactoryExample polymorphicFactoryExample = new PolymorphicFactoryExample();
        polymorphicFactoryExample = polymorphicFactoryExample.internal().addBackReferences();
        FactoryPolymorphicReferenceAttributeTest.ErrorPrinterFactory2 errorPrinterFactory = new FactoryPolymorphicReferenceAttributeTest.ErrorPrinterFactory2();
        polymorphicFactoryExample.referenceList.add(errorPrinterFactory);
        Assertions.assertEquals(errorPrinterFactory,new ArrayList<>(polymorphicFactoryExample.reference.internal_possibleValues()).get(0));
    }

    public static class FactoryPolymorphic extends SimpleFactoryBase<Void,FactoryPolymorphic> {
        FactoryPolymorphicReferenceListAttribute<ExampleFactoryA,Printer> attribute = new FactoryPolymorphicReferenceListAttribute<ExampleFactoryA,Printer>().setupUnsafe(Printer.class, ErrorPrinterFactory.class, OutPrinterFactory.class);

        @Override
        public Void createImpl() {
            return null;
        }
    }

    @Test
    public void test_json(){
        FactoryPolymorphic factoryPolymorphic = new FactoryPolymorphic();
        factoryPolymorphic.attribute.add(new ErrorPrinterFactory());

        ObjectMapperBuilder.build().copy(factoryPolymorphic);
    }
}