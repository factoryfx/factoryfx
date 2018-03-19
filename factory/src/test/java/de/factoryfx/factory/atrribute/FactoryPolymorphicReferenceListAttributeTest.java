package de.factoryfx.factory.atrribute;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.testfactories.poly.ErrorPrinterFactory;
import de.factoryfx.factory.testfactories.poly.OutPrinterFactory;
import de.factoryfx.factory.testfactories.poly.Printer;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class FactoryPolymorphicReferenceListAttributeTest {
    @Test
    public void test_select(){
        PolymorphicFactoryExample polymorphicFactoryExample = new PolymorphicFactoryExample();
        ErrorPrinterFactory errorPrinterFactory = new ErrorPrinterFactory();
        polymorphicFactoryExample.referenceList.add(errorPrinterFactory);
        polymorphicFactoryExample = polymorphicFactoryExample.utility().prepareUsableCopy();

        Assert.assertEquals(polymorphicFactoryExample.referenceList.get(0),new ArrayList<>(polymorphicFactoryExample.reference.internal_possibleValues()).get(0));
    }

    @Test
    public void test_new_value(){
        PolymorphicFactoryExample polymorphicFactoryExample = new PolymorphicFactoryExample();
        polymorphicFactoryExample = polymorphicFactoryExample.utility().prepareUsableCopy();

        List<FactoryBase<? extends Printer, ?>> factoryBases = polymorphicFactoryExample.referenceList.internal_createNewPossibleValues();
        Assert.assertEquals(ErrorPrinterFactory.class,new ArrayList<>(factoryBases).get(0).getClass());
        Assert.assertEquals(OutPrinterFactory.class,new ArrayList<>(polymorphicFactoryExample.reference.internal_createNewPossibleValues()).get(1).getClass());
    }

    @Test(expected=IllegalArgumentException.class)
    public void test_setupUnsafe_validation(){
        new FactoryPolymorphicReferenceListAttribute<Printer>().setupUnsafe(Printer.class,String.class);
    }

    @Test
    public void test_setupUnsafe_validation_happy_case(){
        new FactoryPolymorphicReferenceListAttribute<Printer>().setupUnsafe(Printer.class,ErrorPrinterFactory.class);
    }

    @Test
    public void test_generatorInfo_safe(){
        FactoryPolymorphicReferenceListAttribute<Printer> attribute = new FactoryPolymorphicReferenceListAttribute<Printer>().setup(Printer.class, ErrorPrinterFactory.class, OutPrinterFactory.class);
        Assert.assertEquals(ErrorPrinterFactory.class,attribute.internal_possibleFactoriesClasses().get(0));
        Assert.assertEquals(OutPrinterFactory.class,attribute.internal_possibleFactoriesClasses().get(1));
    }

    @Test
    public void test_generatorInfo_unsafe(){
        FactoryPolymorphicReferenceListAttribute<Printer> attribute = new FactoryPolymorphicReferenceListAttribute<Printer>().setupUnsafe(Printer.class, ErrorPrinterFactory.class, OutPrinterFactory.class);
        Assert.assertEquals(ErrorPrinterFactory.class,attribute.internal_possibleFactoriesClasses().get(0));
        Assert.assertEquals(OutPrinterFactory.class,attribute.internal_possibleFactoriesClasses().get(1));
    }


    @Test
    public void test_generatorInfo_constructor(){
        FactoryPolymorphicReferenceListAttribute<Printer> attribute = new FactoryPolymorphicReferenceListAttribute<>(Printer.class, ErrorPrinterFactory.class, OutPrinterFactory.class);
        Assert.assertEquals(ErrorPrinterFactory.class,attribute.internal_possibleFactoriesClasses().get(0));
        Assert.assertEquals(OutPrinterFactory.class,attribute.internal_possibleFactoriesClasses().get(1));
    }

    @Test
    public void test_set(){
        PolymorphicFactoryExample polymorphicFactoryExample = new PolymorphicFactoryExample();
        polymorphicFactoryExample = polymorphicFactoryExample.internal().prepareUsableCopy();
        FactoryPolymorphicReferenceAttributeTest.ErrorPrinterFactory2 errorPrinterFactory = new FactoryPolymorphicReferenceAttributeTest.ErrorPrinterFactory2();
        polymorphicFactoryExample.referenceList.add(errorPrinterFactory);
        Assert.assertEquals(errorPrinterFactory,new ArrayList<>(polymorphicFactoryExample.reference.internal_possibleValues()).get(0));
    }
}