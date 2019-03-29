package io.github.factoryfx.factory.attribute;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryReferenceAttribute;
import io.github.factoryfx.factory.attribute.dependency.FactoryReferenceListAttribute;
import io.github.factoryfx.factory.attribute.types.StringAttribute;
import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;
import io.github.factoryfx.factory.merge.testdata.ExampleDataA;
import io.github.factoryfx.factory.merge.testdata.ExampleDataB;
import io.github.factoryfx.factory.merge.testdata.ExampleDataC;
import io.github.factoryfx.factory.metadata.FactoryMetadataManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FactoryMetadataTest {

    @Test
    public void test_json(){
        ExampleDataA exampleDataA = new ExampleDataA();
        exampleDataA.referenceAttribute.set(new ExampleDataB());

        ExampleDataA copy = ObjectMapperBuilder.build().copy(exampleDataA);
        Assertions.assertNotNull(copy);
    }

    @Test
    public void test_default_value_json_inside_data(){
        ExampleReferenceData value = new ExampleReferenceData();
        value.attribute.set(new ExampleDataA());
        ExampleReferenceData copy = ObjectMapperBuilder.build().copy(value);
        Assertions.assertNotNull(copy.attribute.get());
    }

    public static class CustomReferenceAttribute<L , F extends FactoryBase<L,ExampleDataA>> extends FactoryReferenceAttribute<ExampleDataA,L,F> {

    }

    public static class ExampleReferenceData extends FactoryBase<Void,ExampleDataA> {
        public final StringAttribute valueAttribute = new StringAttribute();
        public final FactoryReferenceAttribute<ExampleDataA,Void,ExampleDataA> attribute = new FactoryReferenceAttribute<>();
        public final FactoryReferenceListAttribute<ExampleDataA,Void, ExampleDataC> attributeList = new FactoryReferenceListAttribute<>();
        public final CustomReferenceAttribute<Void, ExampleDataC> customReferenceAttribute = new CustomReferenceAttribute<>();
    }

    @Test
    public void test_setReferenceClass(){
        ExampleReferenceData data = new ExampleReferenceData();
        Assertions.assertNull(data.attribute.internal_getReferenceClass());
        Assertions.assertNull(data.attributeList.internal_getReferenceClass());
        FactoryMetadataManager.getMetadata(ExampleReferenceData.class).setAttributeReferenceClasses(data);
        Assertions.assertEquals(ExampleDataA.class,data.attribute.internal_getReferenceClass());
        Assertions.assertEquals(ExampleDataC.class,data.attributeList.internal_getReferenceClass());
    }

    @Test
    public void test_setReferenceClass_attribute_inheritance(){
        ExampleReferenceData data = new ExampleReferenceData();
        Assertions.assertNull(data.customReferenceAttribute.internal_getReferenceClass());
        FactoryMetadataManager.getMetadata(ExampleReferenceData.class).setAttributeReferenceClasses(data);
        Assertions.assertEquals(ExampleDataC.class,data.customReferenceAttribute.internal_getReferenceClass());
    }

    public static class ExampleReferenceDataGenericRoot extends FactoryBase<Void,ExampleReferenceDataGenericRoot> {
        public final FactoryReferenceAttribute<ExampleReferenceDataGenericRoot,Void,ExampleReferenceGeneric<ExampleReferenceDataGenericRoot>> attribute = new FactoryReferenceAttribute<>();
        public final FactoryReferenceAttribute<ExampleReferenceDataGenericRoot,Void,ExampleReferenceGeneric2<ExampleReferenceDataGenericRoot,ExampleReferenceGeneric<ExampleReferenceDataGenericRoot>>> attribute2 = new FactoryReferenceAttribute<>();
    }

    public static class ExampleReferenceGeneric<R extends FactoryBase<?,R>> extends FactoryBase<Void,R> {
        public final StringAttribute valueAttribute = new StringAttribute();
    }
    public static class ExampleReferenceGeneric2<R extends FactoryBase<?,R>,T extends ExampleReferenceGeneric<R>> extends FactoryBase<Void,R> {
        public final StringAttribute valueAttribute = new StringAttribute();
    }

    @Test
    public void test_setReferenceClass_genericfactory(){
        ExampleReferenceDataGenericRoot data = new ExampleReferenceDataGenericRoot();
        Assertions.assertNull(data.attribute.internal_getReferenceClass());
        Assertions.assertNull(data.attribute2.internal_getReferenceClass());
        FactoryMetadataManager.getMetadata(ExampleReferenceDataGenericRoot.class).setAttributeReferenceClasses(data);
        Assertions.assertEquals(ExampleReferenceGeneric.class,data.attribute.internal_getReferenceClass());
        Assertions.assertEquals(ExampleReferenceGeneric2.class,data.attribute2.internal_getReferenceClass());
    }
}
