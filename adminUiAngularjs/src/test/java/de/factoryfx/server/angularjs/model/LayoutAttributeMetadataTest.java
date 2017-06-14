package de.factoryfx.server.angularjs.model;

import java.util.Locale;

import de.factoryfx.server.angularjs.integration.example.ExampleEnum;
import de.factoryfx.data.attribute.types.EnumAttribute;
import org.junit.Assert;
import org.junit.Test;

public class LayoutAttributeMetadataTest {


    @Test
    public void test_enum(){
        WebGuiAttributeMetadata webGuiAttributeMetadata = new WebGuiAttributeMetadata(new EnumAttribute<>(ExampleEnum.class), Locale.ENGLISH);
        Assert.assertEquals(3, webGuiAttributeMetadata.dataType.enumValues.size());
        Assert.assertEquals("EXAMPLE_1", webGuiAttributeMetadata.dataType.enumValues.get(0));
        Assert.assertEquals("EXAMPLE_2", webGuiAttributeMetadata.dataType.enumValues.get(1));
        Assert.assertEquals("EXAMPLE_3", webGuiAttributeMetadata.dataType.enumValues.get(2));
    }

}