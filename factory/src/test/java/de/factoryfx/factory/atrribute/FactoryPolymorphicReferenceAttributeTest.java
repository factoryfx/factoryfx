package de.factoryfx.factory.atrribute;

import de.factoryfx.factory.testfactories.poly.ErrorPrinterFactory;
import de.factoryfx.factory.testfactories.poly.OutPrinterFactory;
import de.factoryfx.factory.testfactories.poly.Printer;
import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.SimpleFactoryBase;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class FactoryPolymorphicReferenceAttributeTest {

    @Test
    public void test_json(){
        FactoryPolymorphicReferenceAttribute<Printer> attribute = new FactoryPolymorphicReferenceAttribute<>();
        attribute.set(new ErrorPrinterFactory());
        ObjectMapperBuilder.build().copy(attribute);
    }

    public static class ReferenceFactory extends SimpleFactoryBase<Object,Void>{
        public final FactoryPolymorphicReferenceAttribute<Printer> reference = new FactoryPolymorphicReferenceAttribute<Printer>().setup(Printer.class,ErrorPrinterFactory.class,OutPrinterFactory.class);

        @Override
        public Object createImpl() {
            reference.instance().print();
            return new Object();
        }
    }

    @Test
    public void test_select(){
        ReferenceFactory referenceFactory = new ReferenceFactory();
        referenceFactory = referenceFactory.internal().prepareUsableCopy();
        ErrorPrinterFactory errorPrinterFactory = new ErrorPrinterFactory();
        referenceFactory.reference.set(errorPrinterFactory);
        Assert.assertEquals(errorPrinterFactory,new ArrayList<>(referenceFactory.reference.internal_possibleValues()).get(0));
    }

    @Test
    public void test_new_value(){
        ReferenceFactory referenceFactory = new ReferenceFactory();
        referenceFactory = referenceFactory.internal().prepareUsableCopy();

        List<FactoryBase<Printer, ?>> factoryBases = referenceFactory.reference.internal_createNewPossibleValues();
        Assert.assertEquals(ErrorPrinterFactory.class,new ArrayList<>(factoryBases).get(0).getClass());
        Assert.assertEquals(OutPrinterFactory.class,new ArrayList<>(referenceFactory.reference.internal_createNewPossibleValues()).get(1).getClass());
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
}