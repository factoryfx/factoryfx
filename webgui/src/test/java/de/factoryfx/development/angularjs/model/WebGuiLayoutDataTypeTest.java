package de.factoryfx.development.angularjs.model;

import de.factoryfx.development.angularjs.integration.example.ExampleEnum;
import org.junit.Assert;
import org.junit.Test;

public class WebGuiLayoutDataTypeTest {

    @Test
    public void test_enum(){
        WebGuiDataType dataType = new WebGuiDataType(ExampleEnum.class);
        Assert.assertEquals("Enum",dataType.dataType);
        Assert.assertEquals(3,dataType.enumValues.size());
    }

}