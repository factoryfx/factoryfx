package de.factoryfx.development.angularjs.model;

import java.util.Locale;

import de.factoryfx.development.angularjs.integration.ExampleFactoryA;
import de.factoryfx.factory.attribute.AttributeMetadata;
import de.factoryfx.factory.attribute.EnumAttribute;
import org.junit.Assert;
import org.junit.Test;

public class WebGuiAttributeMetadataTest {


    @Test
    public void test_enum(){
        WebGuiAttributeMetadata webGuiAttributeMetadata = new WebGuiAttributeMetadata(new AttributeMetadata(), Locale.ENGLISH,new EnumAttribute<>(ExampleFactoryA.ExampleEnum.class,new AttributeMetadata()));
        Assert.assertEquals(3,webGuiAttributeMetadata.enumValues.size());
        Assert.assertEquals("EXAMPLE_1",webGuiAttributeMetadata.enumValues.get(0));
        Assert.assertEquals("EXAMPLE_2",webGuiAttributeMetadata.enumValues.get(1));
        Assert.assertEquals("EXAMPLE_3",webGuiAttributeMetadata.enumValues.get(2));
    }

}