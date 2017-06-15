package de.factoryfx.data.attribute;

import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.data.merge.testfactories.ExampleFactoryA;
import org.junit.Assert;
import org.junit.Test;

import java.util.Locale;

public class AttributeJsonWrapperSpecialTest {


    @Test
    public void test_ref_List() {
        DataReferenceListAttribute<ExampleFactoryA> referenceListAttribute= new DataReferenceListAttribute<>(ExampleFactoryA.class);
        ExampleFactoryA value = new ExampleFactoryA();
        value.stringAttribute.set("XXX");
        referenceListAttribute.add(value);

        final AttributeJsonWrapper copy = ObjectMapperBuilder.build().copy(new AttributeJsonWrapper(referenceListAttribute,""));
        Assert.assertEquals("XXX",((ExampleFactoryA)((ReferenceListAttribute)copy.createAttribute()).get(0)).stringAttribute.get());
    }

    @Test
    public void test_label() {
        StringAttribute stringAttribute= new StringAttribute().en("123");

        final AttributeJsonWrapper copy = ObjectMapperBuilder.build().copy(new AttributeJsonWrapper(stringAttribute,""));
        Assert.assertEquals("123",((StringAttribute)copy.createAttribute()).internal_getPreferredLabelText(Locale.ENGLISH));
    }
}