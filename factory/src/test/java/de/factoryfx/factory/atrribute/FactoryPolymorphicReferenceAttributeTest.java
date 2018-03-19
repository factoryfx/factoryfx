package de.factoryfx.factory.atrribute;

import de.factoryfx.factory.PolymorphicFactoryBase;
import de.factoryfx.factory.testfactories.poly.ErrorPrinter;
import de.factoryfx.factory.testfactories.poly.ErrorPrinterFactory;
import de.factoryfx.factory.testfactories.poly.OutPrinterFactory;
import de.factoryfx.factory.testfactories.poly.Printer;
import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.factory.FactoryBase;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class FactoryPolymorphicReferenceAttributeTest {

    @Test
    public void test_json(){
        FactoryPolymorphicReferenceAttribute<Printer> attribute = new FactoryPolymorphicReferenceAttribute<>();
        attribute.set(new ErrorPrinterFactory());

        System.out.println(ObjectMapperBuilder.build().writeValueAsString(attribute));
        ObjectMapperBuilder.build().copy(attribute);
    }

    @Test
    public void test_select(){
        PolymorphicFactoryExample polymorphicFactoryExample = new PolymorphicFactoryExample();
        polymorphicFactoryExample = polymorphicFactoryExample.internal().prepareUsableCopy();
        ErrorPrinterFactory errorPrinterFactory = new ErrorPrinterFactory();
        polymorphicFactoryExample.reference.set(errorPrinterFactory);
        Assert.assertEquals(errorPrinterFactory,new ArrayList<>(polymorphicFactoryExample.reference.internal_possibleValues()).get(0));
    }

    @Test
    public void test_new_value(){
        PolymorphicFactoryExample polymorphicFactoryExample = new PolymorphicFactoryExample();
        polymorphicFactoryExample = polymorphicFactoryExample.internal().prepareUsableCopy();

        List<FactoryBase<? extends Printer, ?>> factoryBases = polymorphicFactoryExample.reference.internal_createNewPossibleValues();
        Assert.assertEquals(ErrorPrinterFactory.class,new ArrayList<>(factoryBases).get(0).getClass());
        Assert.assertEquals(OutPrinterFactory.class,new ArrayList<>(polymorphicFactoryExample.reference.internal_createNewPossibleValues()).get(1).getClass());
    }

    @Test(expected=IllegalArgumentException.class)
    public void test_setupUnsafe_validation(){
        new FactoryPolymorphicReferenceAttribute<Printer>().setupUnsafe(Printer.class,String.class);
    }

    @Test
    public void test_setupUnsafe_validation_happy_case(){
        new FactoryPolymorphicReferenceAttribute<Printer>().setupUnsafe(Printer.class,ErrorPrinterFactory.class);
    }

    @Test
    public void test_generatorInfo_safe(){
        FactoryPolymorphicReferenceAttribute<Printer> attribute = new FactoryPolymorphicReferenceAttribute<Printer>().setup(Printer.class, ErrorPrinterFactory.class, OutPrinterFactory.class);
        Assert.assertEquals(ErrorPrinterFactory.class,attribute.internal_possibleFactoriesClasses().get(0));
        Assert.assertEquals(OutPrinterFactory.class,attribute.internal_possibleFactoriesClasses().get(1));
    }

    @Test
    public void test_generatorInfo_unsafe(){
        FactoryPolymorphicReferenceAttribute<Printer> attribute = new FactoryPolymorphicReferenceAttribute<Printer>().setupUnsafe(Printer.class, ErrorPrinterFactory.class, OutPrinterFactory.class);
        Assert.assertEquals(ErrorPrinterFactory.class,attribute.internal_possibleFactoriesClasses().get(0));
        Assert.assertEquals(OutPrinterFactory.class,attribute.internal_possibleFactoriesClasses().get(1));
    }


    @Test
    public void test_generatorInfo_constructor(){
        FactoryPolymorphicReferenceAttribute<Printer> attribute = new FactoryPolymorphicReferenceAttribute<>(Printer.class, ErrorPrinterFactory.class, OutPrinterFactory.class);
        Assert.assertEquals(ErrorPrinterFactory.class,attribute.internal_possibleFactoriesClasses().get(0));
        Assert.assertEquals(OutPrinterFactory.class,attribute.internal_possibleFactoriesClasses().get(1));
    }

    public static class ErrorPrinterFactory2 extends PolymorphicFactoryBase<ErrorPrinter,Void> {
        @Override
        public ErrorPrinter createImpl() {
            return new ErrorPrinter();
        }
    }

    @Test
    public void test_set(){
        PolymorphicFactoryExample polymorphicFactoryExample = new PolymorphicFactoryExample();
        polymorphicFactoryExample = polymorphicFactoryExample.internal().prepareUsableCopy();
        ErrorPrinterFactory2 errorPrinterFactory = new ErrorPrinterFactory2();
        polymorphicFactoryExample.reference.set(errorPrinterFactory);
        Assert.assertEquals(errorPrinterFactory,new ArrayList<>(polymorphicFactoryExample.reference.internal_possibleValues()).get(0));
    }
}