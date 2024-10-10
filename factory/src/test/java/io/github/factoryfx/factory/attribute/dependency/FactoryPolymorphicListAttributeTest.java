package io.github.factoryfx.factory.attribute.dependency;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.github.factoryfx.factory.FactoryTreeBuilderBasedAttributeSetup;
import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.builder.FactoryTreeBuilder;
import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;
import io.github.factoryfx.factory.metadata.FactoryMetadataManager;
import io.github.factoryfx.factory.testfactories.poly.ErrorPrinterFactory;
import io.github.factoryfx.factory.testfactories.poly.Printer;

public class FactoryPolymorphicListAttributeTest {

    @Test
    public void test_setupUnsafe_validation_happy_case(){
        new FactoryPolymorphicListAttribute<Printer>();
    }

    public static class FactoryPolymorphic extends SimpleFactoryBase<Void,FactoryPolymorphic> {
        FactoryPolymorphicListAttribute<Printer> attribute = new FactoryPolymorphicListAttribute<Printer>();

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
        root.internal().setFactoryTreeBuilderBasedAttributeSetupForRoot(new FactoryTreeBuilderBasedAttributeSetup<>(builder));
        root.referenceList.internal_createNewPossibleValues(FactoryMetadataManager.getMetadata(PolymorphicFactoryExample.class).getAttributeMetadata(f->f.referenceList));
    }

    @Test
    public void test_referenclass(){
        Assertions.assertNull(FactoryMetadataManager.getMetadata(PolymorphicFactoryExample.class).getAttributeMetadata(f->f.referenceList).referenceClass);//Reference class can' be determined from the generic parameter
    }
}