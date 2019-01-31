package de.factoryfx.data.attribute;

import java.util.List;
import java.util.Locale;

import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.primitive.BooleanAttribute;
import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.data.jackson.SimpleObjectMapper;
import de.factoryfx.data.merge.testdata.ExampleDataA;
import de.factoryfx.data.merge.testdata.ExampleDataB;
import de.factoryfx.data.merge.testdata.ExampleDataC;
import de.factoryfx.data.validation.ValidationError;
import org.junit.Assert;
import org.junit.Test;

public class AttributeTest {
    public static class ValidationExampleFactory extends Data {
        public final StringAttribute stringAttribute= new StringAttribute().labelText("ExampleA1");
    }

    @Test
    public void test_validation(){
        ValidationExampleFactory validationExampleFactory = new ValidationExampleFactory();
        validationExampleFactory.stringAttribute.set("");
        List<ValidationError> validationErrors = validationExampleFactory.internal().validateFlat();
        Assert.assertEquals(1, validationErrors.size());

        validationExampleFactory.stringAttribute.set("ssfdfdsdf");
        validationErrors = validationExampleFactory.internal().validateFlat();
        Assert.assertEquals(0, validationErrors.size());
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


//        Assert.assertEquals(null,readed.stringAttribute);
//        Assert.assertEquals(null,readed.referenceAttribute.en);
//        Assert.assertEquals(null,readed.referenceListAttribute.en);
//        Assert.assertEquals(null,readed.referenceAttribute.get().stringAttribute);
//        Assert.assertEquals(null,readed.referenceListAttribute.get(0).stringAttribute);

        readed = readed.internal().addBackReferences();

        Assert.assertEquals("ExampleA1",readed.stringAttribute.internal_getPreferredLabelText(Locale.ENGLISH));
        Assert.assertEquals("ExampleA2",readed.referenceAttribute.internal_getPreferredLabelText(Locale.ENGLISH));
        Assert.assertEquals("ExampleA3",readed.referenceListAttribute.internal_getPreferredLabelText(Locale.ENGLISH));
        Assert.assertEquals("ExampleB1",readed.referenceAttribute.get().stringAttribute.internal_getPreferredLabelText(Locale.ENGLISH));
        Assert.assertEquals("ExampleB1",readed.referenceListAttribute.get(0).stringAttribute.internal_getPreferredLabelText(Locale.ENGLISH));

        Assert.assertEquals(exampleFactoryA.getId(),readed.getId());
    }

    @Test
    public void test_labelText(){
        StringAttribute stringAttribute = new StringAttribute();
        stringAttribute.labelText("test1",Locale.KOREA);
        Assert.assertEquals("test1",stringAttribute.internal_getPreferredLabelText(Locale.KOREA));
    }

    @Test
    public void test_tooltipText_de(){
        StringAttribute stringAttribute = new StringAttribute().tooltipDe("hallo");
        Assert.assertEquals("hallo",stringAttribute.internal_getPreferredTooltipText(Locale.GERMAN));
    }

    @Test
    public void test_tooltipText_en(){
        StringAttribute stringAttribute = new StringAttribute().tooltipEn("hi");
        Assert.assertEquals("hi",stringAttribute.internal_getPreferredTooltipText(Locale.ENGLISH));
    }


    @Test
    public void test_dynamic_readonly(){
        DynamicReadOnlyData dynamicReadOnlyData = new DynamicReadOnlyData();
        dynamicReadOnlyData.barred.set(false);
        DynamicReadOnlyData2 data2 = new DynamicReadOnlyData2();
        data2.attribute.set("123");
        dynamicReadOnlyData.strangeList.add(data2);

        dynamicReadOnlyData.internal().addBackReferences();

        Assert.assertFalse(data2.attribute.internal_isUserReadOnly());
        dynamicReadOnlyData.barred.set(true);
        Assert.assertTrue(data2.attribute.internal_isUserReadOnly());
    }

    public static class DynamicReadOnlyData extends Data {
        public final BooleanAttribute barred = new BooleanAttribute();
        public final DataReferenceListAttribute<DynamicReadOnlyData2> strangeList = new DataReferenceListAttribute<>(DynamicReadOnlyData2.class);
    }

    public static class DynamicReadOnlyData2 extends Data {
        public final StringAttribute attribute= new StringAttribute().userReadOnly(()->((DynamicReadOnlyData)internal().getRoot()).barred.get());
    }

}