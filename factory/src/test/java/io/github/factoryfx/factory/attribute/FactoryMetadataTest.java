package io.github.factoryfx.factory.attribute;

import io.github.factoryfx.factory.AttributeMetadataVisitor;
import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;
import io.github.factoryfx.factory.attribute.dependency.FactoryListAttribute;
import io.github.factoryfx.factory.attribute.types.EnumAttribute;
import io.github.factoryfx.factory.attribute.types.EnumAttributeTest;
import io.github.factoryfx.factory.attribute.types.EnumListAttribute;
import io.github.factoryfx.factory.attribute.types.StringAttribute;
import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;
import io.github.factoryfx.factory.merge.testdata.ExampleDataA;
import io.github.factoryfx.factory.merge.testdata.ExampleDataB;
import io.github.factoryfx.factory.merge.testdata.ExampleDataC;
import io.github.factoryfx.factory.metadata.AttributeMetadata;
import io.github.factoryfx.factory.metadata.FactoryMetadataManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

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

    public static class CustomReferenceAttribute<L , F extends FactoryBase<L,ExampleDataA>> extends FactoryAttribute<L,F> {

    }

    public static class ExampleReferenceData extends FactoryBase<Void,ExampleDataA> {
        public final StringAttribute valueAttribute = new StringAttribute();
        public final FactoryAttribute<Void,ExampleDataA> attribute = new FactoryAttribute<>();
        public final FactoryListAttribute<Void, ExampleDataC> attributeList = new FactoryListAttribute<>();
        public final CustomReferenceAttribute<Void, ExampleDataC> customReferenceAttribute = new CustomReferenceAttribute<>();
    }


    public static class ExampleReferenceDataGenericRoot extends FactoryBase<Void,ExampleReferenceDataGenericRoot> {
        public final FactoryAttribute<Void,ExampleReferenceGeneric<ExampleReferenceDataGenericRoot>> attribute = new FactoryAttribute<>();
        public final FactoryAttribute<Void,ExampleReferenceGeneric2<ExampleReferenceDataGenericRoot,ExampleReferenceGeneric<ExampleReferenceDataGenericRoot>>> attribute2 = new FactoryAttribute<>();
    }

    public static class ExampleReferenceGeneric<R extends FactoryBase<?,R>> extends FactoryBase<Void,R> {
        public final StringAttribute valueAttribute = new StringAttribute();
    }
    public static class ExampleReferenceGeneric2<R extends FactoryBase<?,R>,T extends ExampleReferenceGeneric<R>> extends FactoryBase<Void,R> {
        public final StringAttribute valueAttribute = new StringAttribute();
    }



    public static class ExampleEnumAttributeFactory extends FactoryBase<Void,ExampleEnumAttributeFactory> {
        public final EnumAttribute<EnumAttributeTest.TestEnum> enumAttribute= new EnumAttribute<>();
    }

    @Test
    public void test_visitEnumMetadata(){
        List<Class<?>> enumlist = new ArrayList<>();
        FactoryMetadataManager.getMetadata(ExampleEnumAttributeFactory.class).visitAttributeMetadata(metadata -> enumlist.add(metadata.enumClass));
        Assertions.assertEquals(EnumAttributeTest.TestEnum.class,enumlist.get(0));

    }

    public static class ExampleEnumListAttributeFactory extends FactoryBase<Void,ExampleEnumListAttributeFactory> {
        public final EnumListAttribute<EnumAttributeTest.TestEnum> enumAttribute= new EnumListAttribute<>();
    }

    @Test
    public void test_visitEnumList(){
        FactoryMetadataManager.getMetadata(ExampleEnumListAttributeFactory.class).visitAttributeMetadata(metadata -> {
            Assertions.assertEquals(EnumAttributeTest.TestEnum.class,metadata.enumClass);
        });
    }

    public static class ExampleFactoryAttributeFactory extends FactoryBase<Void,ExampleFactoryAttributeFactory> {
        public final FactoryAttribute<Void,ExampleFactoryAttributeFactory> factoryAttribute= new FactoryAttribute<>();
    }

    @Test
    public void test_visitRef(){
        FactoryMetadataManager.getMetadata(ExampleFactoryAttributeFactory.class).visitAttributeMetadata(metadata -> {
            Assertions.assertEquals(ExampleFactoryAttributeFactory.class,metadata.referenceClass);
        });
    }

    @Test
    public void test_visit_metadata_caching(){
        List<String> counter=new ArrayList<>();
        FactoryMetadataManager.getMetadata(ExampleFactoryAttributeFactory.class).visitAttributeMetadata(metadata -> {
            counter.add("1");
        });

        FactoryMetadataManager.getMetadata(ExampleFactoryAttributeFactory.class).visitAttributeMetadata(metadata -> {
            counter.add("2");
        });
        Assertions.assertEquals(List.of("1","2"),counter);
    }


    public static class NestedFactory extends FactoryBase<Void, NestedFactory> {

    }

    @Test
    public void test_newInstanceNestedClass(){
        FactoryMetadataManager.getMetadata(ExampleFactoryAttributeFactory.class).newInstance();
    }



}
