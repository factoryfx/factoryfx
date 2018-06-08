package de.factoryfx.data.attribute.time;

import de.factoryfx.data.attribute.primitive.ShortAttribute;
import de.factoryfx.data.jackson.ObjectMapperBuilder;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.*;

public class LocalDateAttributeTest {

    @Test
    public void test_json(){
        LocalDateAttribute attribute = new LocalDateAttribute();
        LocalDate value = LocalDate.now();
        attribute.set(value);
        LocalDateAttribute copy = ObjectMapperBuilder.build().copy(attribute);
        Assert.assertEquals(value,copy.get());
    }


}