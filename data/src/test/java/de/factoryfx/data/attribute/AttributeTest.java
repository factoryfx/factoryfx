package de.factoryfx.data.attribute;

import java.util.List;
import java.util.Locale;

import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.data.jackson.SimpleObjectMapper;
import de.factoryfx.data.merge.testfactories.ExampleFactoryA;
import de.factoryfx.data.merge.testfactories.ExampleFactoryB;
import de.factoryfx.data.merge.testfactories.ExampleFactoryC;
import de.factoryfx.data.validation.StringRequired;
import de.factoryfx.data.validation.ValidationError;
import org.junit.Assert;
import org.junit.Test;

public class AttributeTest {
    public class ValidationExampleFactory extends Data {
        public final StringAttribute stringAttribute= new StringAttribute().labelText("ExampleA1").validation(new StringRequired());
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
        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
        ExampleFactoryB exampleFactoryB = new ExampleFactoryB();
        ExampleFactoryC exampleFactoryC = new ExampleFactoryC();
        exampleFactoryB.referenceAttributeC.set(exampleFactoryC);
        exampleFactoryA.referenceAttribute.set(exampleFactoryB);

        exampleFactoryA.referenceListAttribute.add(new ExampleFactoryB());

        SimpleObjectMapper mapper = ObjectMapperBuilder.build();
        String string = mapper.writeValueAsString(exampleFactoryA);
        ExampleFactoryA readed = ObjectMapperBuilder.buildNewObjectMapper().readValue(string,ExampleFactoryA.class);


        Assert.assertEquals(null,readed.stringAttribute);
        Assert.assertEquals(null,readed.referenceAttribute.en);
        Assert.assertEquals(null,readed.referenceListAttribute.en);
        Assert.assertEquals(null,readed.referenceAttribute.get().stringAttribute);
        Assert.assertEquals(null,readed.referenceListAttribute.get(0).stringAttribute);

        readed = readed.internal().prepareUsableCopy();

        Assert.assertEquals("ExampleA1",readed.stringAttribute.internal_getPreferredLabelText(Locale.ENGLISH));
        Assert.assertEquals("ExampleA2",readed.referenceAttribute.internal_getPreferredLabelText(Locale.ENGLISH));
        Assert.assertEquals("ExampleA3",readed.referenceListAttribute.internal_getPreferredLabelText(Locale.ENGLISH));
        Assert.assertEquals("ExampleB1",readed.referenceAttribute.get().stringAttribute.internal_getPreferredLabelText(Locale.ENGLISH));
        Assert.assertEquals("ExampleB1",readed.referenceListAttribute.get(0).stringAttribute.internal_getPreferredLabelText(Locale.ENGLISH));

        Assert.assertEquals(exampleFactoryA.getId(),readed.getId());
    }

}