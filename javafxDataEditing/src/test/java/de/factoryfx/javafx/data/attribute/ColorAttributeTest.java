package de.factoryfx.javafx.data.attribute;

import javafx.scene.paint.Color;
import org.junit.Assert;
import org.junit.Test;

public class ColorAttributeTest {
    @Test
    public void test_happyCAse() {
        ColorAttribute attribute= new ColorAttribute().defaultValue(Color.AQUA);
        Assert.assertEquals(Color.AQUA,attribute.get());
    }
}