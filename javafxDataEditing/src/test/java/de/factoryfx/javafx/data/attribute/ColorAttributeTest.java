package de.factoryfx.javafx.data.attribute;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.factoryfx.data.jackson.ObjectMapperBuilder;
import javafx.scene.paint.Color;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class ColorAttributeTest {
    @Test
    public void test_happyCAse() {
        ColorAttribute attribute= new ColorAttribute().defaultValue(Color.AQUA);
        Assert.assertEquals(Color.AQUA,attribute.get());
    }
}