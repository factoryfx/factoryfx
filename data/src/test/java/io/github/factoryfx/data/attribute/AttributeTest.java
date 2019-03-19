package io.github.factoryfx.data.attribute;

import java.util.List;
import java.util.Locale;

import io.github.factoryfx.data.Data;
import io.github.factoryfx.data.attribute.primitive.BooleanAttribute;
import io.github.factoryfx.data.attribute.types.StringAttribute;
import io.github.factoryfx.data.jackson.ObjectMapperBuilder;
import io.github.factoryfx.data.jackson.SimpleObjectMapper;
import io.github.factoryfx.data.merge.testdata.ExampleDataA;
import io.github.factoryfx.data.merge.testdata.ExampleDataB;
import io.github.factoryfx.data.merge.testdata.ExampleDataC;
import io.github.factoryfx.data.validation.ValidationError;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AttributeTest {
    public static class ValidationExampleFactory extends Data {
        public final StringAttribute stringAttribute= new StringAttribute().labelText("ExampleA1");
    }

    @Test
    public void test_validation(){
        ValidationExampleFactory validationExampleFactory = new ValidationExampleFactory();
        validationExampleFactory.stringAttribute.set("");
        List<ValidationError> validationErrors = validationExampleFactory.internal().validateFlat();
        Assertions.assertEquals(1, validationErrors.size());

        validationExampleFactory.stringAttribute.set("ssfdfdsdf");
        validationErrors = validationExampleFactory.internal().validateFlat();
        Assertions.assertEquals(0, validationErrors.size());
    }

    @Test
    public void test_reconstructMetadataDeepRoot() throws Exception{
        ExampleDataA exampleFactoryA = new ExampleDataA();
        ExampleDataB exampleFactoryB = new ExampleDataB();
        ExampleDataC exampleFactoryC = new ExampleDataC();
        exampleFactoryB.referenceAttributeC.set(exampleFactoryC);
        exampleFactoryA.referenceAttribute.set(exampleFactoryB);

        exampleFactoryA.referenceListAttribute.add(new ExampleDataB());

        SimpleObjectMapper mapper = ObjectMapperBuilder.build();
        String string = mapper.writeValueAsString(exampleFactoryA);
        ExampleDataA readed = ObjectMapperBuilder.buildNewObjectMapper().readValue(string,ExampleDataA.class);


//        Assertions.assertEquals(null,readed.stringAttribute);
//        Assertions.assertEquals(null,readed.referenceAttribute.en);
//        Assertions.assertEquals(null,readed.referenceListAttribute.en);
//        Assertions.assertEquals(null,readed.referenceAttribute.get().stringAttribute);
//        Assertions.assertEquals(null,readed.referenceListAttribute.get(0).stringAttribute);

        readed = readed.internal().addBackReferences();

        Assertions.assertEquals("ExampleA1",readed.stringAttribute.internal_getPreferredLabelText(Locale.ENGLISH));
        Assertions.assertEquals("ExampleA2",readed.referenceAttribute.internal_getPreferredLabelText(Locale.ENGLISH));
        Assertions.assertEquals("ExampleA3",readed.referenceListAttribute.internal_getPreferredLabelText(Locale.ENGLISH));
        Assertions.assertEquals("ExampleB1",readed.referenceAttribute.get().stringAttribute.internal_getPreferredLabelText(Locale.ENGLISH));
        Assertions.assertEquals("ExampleB1",readed.referenceListAttribute.get(0).stringAttribute.internal_getPreferredLabelText(Locale.ENGLISH));

        Assertions.assertEquals(exampleFactoryA.getId(),readed.getId());
    }

    @Test
    public void test_labelText(){
        StringAttribute stringAttribute = new StringAttribute();
        stringAttribute.labelText("test1",Locale.KOREA);
        Assertions.assertEquals("test1",stringAttribute.internal_getPreferredLabelText(Locale.KOREA));
    }

    @Test
    public void test_tooltipText_de(){
        StringAttribute stringAttribute = new StringAttribute().tooltipDe("hallo");
        Assertions.assertEquals("hallo",stringAttribute.internal_getPreferredTooltipText(Locale.GERMAN));
    }

    @Test
    public void test_tooltipText_en(){
        StringAttribute stringAttribute = new StringAttribute().tooltipEn("hi");
        Assertions.assertEquals("hi",stringAttribute.internal_getPreferredTooltipText(Locale.ENGLISH));
    }


    @Test
    public void test_dynamic_readonly(){
        DynamicReadOnlyData dynamicReadOnlyData = new DynamicReadOnlyData();
        dynamicReadOnlyData.barred.set(false);
        DynamicReadOnlyData2 data2 = new DynamicReadOnlyData2();
        data2.attribute.set("123");
        dynamicReadOnlyData.strangeList.add(data2);

        dynamicReadOnlyData.internal().addBackReferences();

        Assertions.assertFalse(data2.attribute.internal_isUserReadOnly());
        dynamicReadOnlyData.barred.set(true);
        Assertions.assertTrue(data2.attribute.internal_isUserReadOnly());
    }

    public static class DynamicReadOnlyData extends Data {
        public final BooleanAttribute barred = new BooleanAttribute();
        public final DataReferenceListAttribute<DynamicReadOnlyData2> strangeList = new DataReferenceListAttribute<>(DynamicReadOnlyData2.class);
    }

    public static class DynamicReadOnlyData2 extends Data {
        public final StringAttribute attribute= new StringAttribute().userReadOnly(()->((DynamicReadOnlyData)internal().getRoot()).barred.get());
    }

}