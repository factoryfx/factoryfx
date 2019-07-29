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

        Assertions.assertEquals(polymorphicFactoryExample.referenceList.get(0),new ArrayList<>(polymorphicFactoryExample.reference.internal_possibleValues()).get(0));
    }

    @Test
    public void test_new_value(){
        PolymorphicFactoryExample polymorphicFactoryExample = new PolymorphicFactoryExample();
        polymorphicFactoryExample.internal().finalise();

        List<FactoryBase<? extends Printer, ExampleFactoryA>> factoryBases = polymorphicFactoryExample.referenceList.internal_createNewPossibleValues();
        Assertions.assertEquals(ErrorPrinterFactory.class,new ArrayList<>(factoryBases).get(0).getClass());
        Assertions.assertEquals(OutPrinterFactory.class,new ArrayList<>(polymorphicFactoryExample.reference.internal_createNewPossibleValues()).get(1).getClass());
    }

    @Test
    public void test_setupUnsafe_validation(){
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new FactoryPolymorphicListAttribute<ExampleFactoryA,Printer>().setupUnsafe(Printer.class, String.class);
        });
    }

    @Test
    public void test_setupUnsafe_validation_happy_case(){
        new FactoryPolymorphicListAttribute<ExampleFactoryA,Printer>().setupUnsafe(Printer.class,ErrorPrinterFactory.class);
    }

    @Test
    public void test_generatorInfo_safe(){
        FactoryPolymorphicListAttribute<ExampleFactoryA,Printer> attribute = new FactoryPolymorphicListAttribute<ExampleFactoryA,Printer>().setup(Printer.class, ErrorPrinterFactory.class, OutPrinterFactory.class);
        Assertions.assertEquals(ErrorPrinterFactory.class,attribute.internal_possibleFactoriesClasses().get(0));
        Assertions.assertEquals(OutPrinterFactory.class,attribute.internal_possibleFactoriesClasses().get(1));
    }

    @Test
    public void test_generatorInfo_unsafe(){
        FactoryPolymorphicListAttribute<ExampleFactoryA,Printer> attribute = new FactoryPolymorphicListAttribute<ExampleFactoryA,Printer>().setupUnsafe(Printer.class, ErrorPrinterFactory.class, OutPrinterFactory.class);
        Assertions.assertEquals(ErrorPrinterFactory.class,attribute.internal_possibleFactoriesClasses().get(0));
        Assertions.assertEquals(OutPrinterFactory.class,attribute.internal_possibleFactoriesClasses().get(1));
    }


    @Test
    public void test_generatorInfo_constructor(){
        FactoryPolymorphicListAttribute<ExampleFactoryA,Printer> attribute = new FactoryPolymorphicListAttribute<>(Printer.class, ErrorPrinterFactory.class, OutPrinterFactory.class);
        Assertions.assertEquals(ErrorPrinterFactory.class,attribute.internal_possibleFactoriesClasses().get(0));
        Assertions.assertEquals(OutPrinterFactory.class,attribute.internal_possibleFactoriesClasses().get(1));
    }

    @Test
    public void test_set(){
        PolymorphicFactoryExample polymorphicFactoryExample = new PolymorphicFactoryExample();
        polymorphicFactoryExample = polymorphicFactoryExample.internal().finalise();
        FactoryPolymorphicAttributeTest.ErrorPrinterFactory2 errorPrinterFactory = new FactoryPolymorphicAttributeTest.ErrorPrinterFactory2();
        polymorphicFactoryExample.referenceList.add(errorPrinterFactory);
        Assertions.assertEquals(errorPrinterFactory,new ArrayList<>(polymorphicFactoryExample.reference.internal_possibleValues()).get(0));
    }

    public static class FactoryPolymorphic extends SimpleFactoryBase<Void,FactoryPolymorphic> {
        FactoryPolymorphicListAttribute<ExampleFactoryA,Printer> attribute = new FactoryPolymorphicListAttribute<ExampleFactoryA,Printer>().setupUnsafe(Printer.class, ErrorPrinterFactory.class, OutPrinterFactory.class);

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
        root.referenceList.internal_createNewPossibleValues();
    }

    @Test
    public void test_referenclass(){
        PolymorphicFactoryExample root = new PolymorphicFactoryExample();
        FactoryMetadataManager.getMetadata(PolymorphicFactoryExample.class).addBackReferencesAndReferenceClassToAttributes(root,root);
        Assertions.assertNull(root.referenceList.clazz);//Reference class can' be determined from the generic parameter
    }
}