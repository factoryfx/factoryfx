package de.factoryfx.data.attribute.types;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class BigDecimalAttributeTest {

    @Test
    public void test_json(){
        BigDecimalAttribute attribute = new BigDecimalAttribute();
        BigDecimal value = BigDecimal.valueOf(1);
        attribute.set(value);
        BigDecimalAttribute copy = ObjectMapperBuilder.build().copy(attribute);
        Assert.assertEquals(value,copy.get());
    }

    @Test
    public void test_pattern(){
        BigDecimalAttribute attribute = new BigDecimalAttribute().decimalFormatPattern("00");
        BigDecimal value = BigDecimal.valueOf(1);
        attribute.set(value);
        Assert.assertEquals("01",new  DecimalFormat(attribute.internal_getDecimalFormatPattern()).format(attribute.get()));
    }

}