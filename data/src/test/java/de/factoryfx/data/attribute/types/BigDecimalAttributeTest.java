package de.factoryfx.data.attribute.types;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class BigDecimalAttributeTest {

    @Test
    public void test_json(){
        BigDecimalAttribute attribute = new BigDecimalAttribute();
        BigDecimal value = BigDecimal.valueOf(1);
        attribute.set(value);
        BigDecimalAttribute copy = ObjectMapperBuilder.build().copy(attribute);
        Assertions.assertEquals(value,copy.get());
    }

    @Test
    public void test_pattern(){
        BigDecimalAttribute attribute = new BigDecimalAttribute().decimalFormatPattern("00");
        BigDecimal value = BigDecimal.valueOf(1);
        attribute.set(value);
        Assertions.assertEquals("01",new  DecimalFormat(attribute.internal_getDecimalFormatPattern()).format(attribute.get()));
    }

}