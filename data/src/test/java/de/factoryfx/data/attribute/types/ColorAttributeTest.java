package de.factoryfx.data.attribute.types;

import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.jackson.ObjectMapperBuilder;
import javafx.scene.paint.Color;
import org.junit.Assert;
import org.junit.Test;

public class ColorAttributeTest {
    @Test
    public void test_json(){
        ColorAttribute attribute= new ColorAttribute(new AttributeMetadata()).defaultValue(Color.AQUA);
        ColorAttribute copy= ObjectMapperBuilder.build().copy(attribute);
        Assert.assertEquals(Color.AQUA,copy.get());
    }
}