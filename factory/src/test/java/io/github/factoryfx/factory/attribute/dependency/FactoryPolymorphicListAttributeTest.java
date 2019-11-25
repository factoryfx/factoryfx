package io.github.factoryfx.factory.attribute.dependency;

import io.github.factoryfx.factory.FactoryTreeBuilderBasedAttributeSetup;
import io.github.factoryfx.factory.builder.FactoryTreeBuilder;
import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;
import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.metadata.FactoryMetadataManager;
import io.github.factoryfx.factory.testfactories.ExampleFactoryA;
import io.github.factoryfx.factory.testfactories.poly.ErrorPrinterFactory;
import io.github.factoryfx.factory.testfactories.poly.OutPrinterFactory;
import io.github.factoryfx.factory.testfactories.poly.Printer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class FactoryPolymorphicListAttributeTest {
    @Test
    public void test_select(){
        PolymorphicFactoryExample polymorphicFactoryExample = new PolymorphicFactoryExample();
        ErrorPrinterFactory errorPrinterFactory = new ErrorPrinterFactory();
        polymorphicFactoryExample.referenceList.add(errorPrinterFactory);
        polymorphicFactoryExample.internal().finalise();

        Assertions.assertEquals(polymorphicFactoryExample.referenceList.get(0),new ArrayList<>(polymorphicFactoryExample.reference.internal_possibleValues(FactoryMetadataManager.getMetadata(PolymorphicFactoryExample.class).getAttributeMetadata(f->f.reference))).get(0));
    }

    @Test
    public void test_new_value(){
        PolymorphicFactoryExample polymorphicFactoryExample = new PolymorphicFactoryExample();
        polymorphicFactoryExample.internal().finalise();

        List<FactoryBase<? extends Printer, ?>> factoryBases = polymorphicFactoryExample.referenceList.internal_createNewPossibleValues(FactoryMetadataManager.getMetadata(PolymorphicFactoryExample.class).getAttributeMetadata(f->f.referenceList));
        Assertions.assertEquals(ErrorPrinterFactory.class,new ArrayList<>(factoryBases).get(0).getClass());
        Assertions.assertEquals(OutPrinterFactory.class,new ArrayList<>(polymorphicFactoryExample.reference.internal_createNewPossibleValues(FactoryMetadataManager.getMetadata(PolymorphicFactoryExample.class).getAttributeMetadata(f->f.reference))).get(1).getClass());
    }

    @Test
    public void test_setupUnsafe_validation(){
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new FactoryPolymorphicListAttribute<Printer>().setupUnsafe(Printer.class, String.class);
        });
    }

    @Test
    public void test_setupUnsafe_validation_happy_case(){
        new FactoryPolymorphicListAttribute<Printer>().setupUnsafe(Printer.class,ErrorPrinterFactory.class);
    }

    @Test
    public void test_generatorInfo_safe(){
        FactoryPolymorphicListAttribute<Printer> attribute = new FactoryPolymorphicListAttribute<Printer>().setup(Printer.class, ErrorPrinterFactory.class, OutPrinterFactory.class);
        Assertions.assertEquals(ErrorPrinterFactory.class,attribute.internal_possibleFactoriesClasses().get(0));
        Assertions.assertEquals(OutPrinterFactory.class,attribute.internal_possibleFactoriesClasses().get(1));
    }

    @Test
    public void test_generatorInfo_unsafe(){
        FactoryPolymorphicListAttribute<Printer> attribute = new FactoryPolymorphicListAttribute<Printer>().setupUnsafe(Printer.class, ErrorPrinterFactory.class, OutPrinterFactory.class);
        Assertions.assertEquals(ErrorPrinterFactory.class,attribute.internal_possibleFactoriesClasses().get(0));
        Assertions.assertEquals(OutPrinterFactory.class,attribute.internal_possibleFactoriesClasses().get(1));
    }


    @Test
    public void test_generatorInfo_constructor(){
        FactoryPolymorphicListAttribute<Printer> attribute = new FactoryPolymorphicListAttribute<>(Printer.class, ErrorPrinterFactory.class, OutPrinterFactory.class);
        Assertions.assertEquals(ErrorPrinterFactory.class,attribute.internal_possibleFactoriesClasses().get(0));
        Assertions.assertEquals(OutPrinterFactory.class,attribute.internal_possibleFactoriesClasses().get(1));
    }

    @Test
    public void test_set(){
        PolymorphicFactoryExample polymorphicFactoryExample = new PolymorphicFactoryExample();
        polymorphicFactoryExample = polymorphicFactoryExample.internal().finalise();
        FactoryPolymorphicAttributeTest.ErrorPrinterFactory2 errorPrinterFactory = new FactoryPolymorphicAttributeTest.ErrorPrinterFactory2();
        polymorphicFactoryExample.referenceList.add(errorPrinterFactory);
        Assertions.assertEquals(errorPrinterFactory,new ArrayList<>(polymorphicFactoryExample.reference.internal_possibleValues(FactoryMetadataManager.getMetadata(PolymorphicFactoryExample.class).getAttributeMetadata(f->f.reference))).get(0));
    }

    public static class FactoryPolymorphic extends SimpleFactoryBase<Void,FactoryPolymorphic> {
        FactoryPolymorphicListAttribute<Printer> attribute = new FactoryPolymorphicListAttribute<Printer>().setupUnsafe(Printer.class, ErrorPrinterFactory.class, OutPrinterFactory.class);

        @Override
        protected Void createImpl() {
            return null;
        }
    }

    @Test
    public void test_json(){
        FactoryPolymorphic factoryPolymorphic = new FactoryPolymorphic();
        factoryPolymorphic.attribute.add(new ErrorPrinterFactory());

        ObjectMapperBuilder.build().copy(factoryPolymorphic);
    }

    @Test
    public void test_newValue_noexception(){
        FactoryTreeBuilder<Object,PolymorphicFactoryExample> builder = new FactoryTreeBuilder<>(PolymorphicFactoryExample.class, ctx -> new PolymorphicFactoryExample());
        PolymorphicFactoryExample root = builder.buildTreeUnvalidated();
        root.internal().serFactoryTreeBuilderBasedAttributeSetupForRoot(new FactoryTreeBuilderBasedAttributeSetup<>(builder));
        root.referenceList.internal_createNewPossibleValues(FactoryMetadataManager.getMetadata(PolymorphicFactoryExample.class).getAttributeMetadata(f->f.referenceList));
    }

    @Test
    public void test_referenclass(){
        Assertions.assertNull(FactoryMetadataManager.getMetadata(PolymorphicFactoryExample.class).getAttributeMetadata(f->f.referenceList).referenceClass);//Reference class can' be determined from the generic parameter
    }
}