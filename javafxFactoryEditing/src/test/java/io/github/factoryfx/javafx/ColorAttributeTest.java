package io.github.factoryfx.javafx;

import io.github.factoryfx.javafx.factory.editor.attribute.ColorAttribute;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ColorAttributeTest {
    @Test
    public void test_happyCAse() {
        ColorAttribute attribute= new ColorAttribute().defaultValue(Color.AQUA);
        Assertions.assertEquals(Color.AQUA,attribute.get());
    }
}