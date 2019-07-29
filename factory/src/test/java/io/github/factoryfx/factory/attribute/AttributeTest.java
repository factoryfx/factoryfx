package io.github.factoryfx.factory.attribute;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryListAttribute;
import io.github.factoryfx.factory.attribute.primitive.BooleanAttribute;
import io.github.factoryfx.factory.attribute.types.StringAttribute;
import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;
import io.github.factoryfx.factory.jackson.SimpleObjectMapper;
import io.github.factoryfx.factory.merge.testdata.ExampleDataA;
import io.github.factoryfx.factory.merge.testdata.ExampleDataB;
import io.github.factoryfx.factory.merge.testdata.ExampleDataC;
import io.github.factoryfx.factory.validation.ValidationError;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class AttributeTest {
    public static class ValidationExampleFactory extends FactoryBase<Void,ValidationExampleFactory> {
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

        readed.internal().finalise();

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

        dynamicReadOnlyData.internal().finalise();

        Assertions.assertFalse(data2.attribute.internal_isUserReadOnly());
        dynamicReadOnlyData.barred.set(true);
        Assertions.assertTrue(data2.attribute.internal_isUserReadOnly());
    }

    public static class DynamicReadOnlyData extends FactoryBase<Void,DynamicReadOnlyData> {
        public final BooleanAttribute barred = new BooleanAttribute();
        public final FactoryListAttribute<Void,DynamicReadOnlyData2> strangeList = new FactoryListAttribute<>();
    }

    public static class DynamicReadOnlyData2 extends FactoryBase<Void,DynamicReadOnlyData> {
        public final StringAttribute attribute= new StringAttribute().userReadOnly(()->((DynamicReadOnlyData)internal().getRoot()).barred.get());
    }


    @Test
    public void test_removeListener_multiple(){
        StringAttribute stringAttribute = new StringAttribute();
        AttributeChangeListener<String, StringAttribute> newListener1 = (attribute, value) -> { };
        AttributeChangeListener<String, StringAttribute> newListener2 = (attribute, value) -> { };
        stringAttribute.internal_addListener(newListener1);
        stringAttribute.internal_addListener(newListener2);

        stringAttribute.internal_removeListener(newListener1);
        stringAttribute.internal_removeListener(newListener2);
        Assertions.assertEquals(0,stringAttribute.internal_getListeners().size());
    }

    @Test
    public void test_removeListener_multiple_called(){
        List<String> called1=new ArrayList<>();
        List<String> called2=new ArrayList<>();

        StringAttribute stringAttribute = new StringAttribute();
        AttributeChangeListener<String, StringAttribute> newListener1 = (attribute, value) -> called1.add("111");
        AttributeChangeListener<String, StringAttribute> newListener2 = (attribute, value) -> called2.add("222");
        stringAttribute.internal_addListener(newListener1);
        Assertions.assertEquals(1,stringAttribute.internal_getListeners().size());
        stringAttribute.internal_addListener(newListener2);
        Assertions.assertEquals(2,stringAttribute.internal_getListeners().size());


        stringAttribute.set("aaa");

        Assertions.assertEquals(1,called1.size());
        Assertions.assertEquals(1,called2.size());

        stringAttribute.set("bbb");

        Assertions.assertEquals(2,called1.size());
        Assertions.assertEquals(2,called2.size());

    }

}