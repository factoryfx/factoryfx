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
    public void test_json() throws IOException {
        ColorAttribute attribute= new ColorAttribute().defaultValue(Color.AQUA);
        ObjectMapper objectMapper = ObjectMapperBuilder.buildNewObjectMapper();
        objectMapper.addMixIn(Color.class, ColorMixInAnnotations.class);


        String s = objectMapper.writeValueAsString(attribute);
        ColorAttribute copy= objectMapper.readValue(s,ColorAttribute.class) ;



        Assert.assertEquals(Color.AQUA,copy.get());
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class ColorMixInAnnotations{
        @JsonCreator
        ColorMixInAnnotations(@JsonProperty("red") double red, @JsonProperty("green") double green, @JsonProperty("blue") double blue, @JsonProperty("opacity") double opacity  ) {

        }
    }
}