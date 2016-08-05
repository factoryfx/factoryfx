package de.factoryfx.development.angularjs.model;

import java.util.Locale;

import de.factoryfx.development.angularjs.integration.ExampleEnum;
import de.factoryfx.factory.attribute.AttributeMetadata;
import de.factoryfx.factory.attribute.util.EnumAttribute;
import org.junit.Assert;
import org.junit.Test;

public class WebGuiAttributeMetadataTest {


    @Test
    public void test_enum(){
        WebGuiAttributeMetadata webGuiAttributeMetadata = new WebGuiAttributeMetadata(new AttributeMetadata(), Locale.ENGLISH,new EnumAttribute<>(ExampleEnum.class,new AttributeMetadata()));
        Assert.assertEquals(3, webGuiAttributeMetadata.dataType.enumValues.size());
        Assert.assertEquals("EXAMPLE_1", webGuiAttributeMetadata.dataType.enumValues.get(0));
        Assert.assertEquals("EXAMPLE_2", webGuiAttributeMetadata.dataType.enumValues.get(1));
        Assert.assertEquals("EXAMPLE_3", webGuiAttributeMetadata.dataType.enumValues.get(2));
    }

}