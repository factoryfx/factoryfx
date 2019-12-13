package io.github.factoryfx.factory.attribute.types;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class BigDecimalAttributeTest {

    @Test
    public void test_json(){
        BigDecimalAttribute attribute = new BigDecimalAttribute();
        BigDecimal value = new BigDecimal("178667868767867867876878678667867868768687.786767");
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


    public static class ExampleBigDecimal extends FactoryBase<Void, ExampleBigDecimal> {
        public final BigDecimalAttribute attribute= new BigDecimalAttribute();
    }

    @Test
    public void test_json_from_browser(){//js can't handle large longs in number type
        ExampleBigDecimal factory = new ExampleBigDecimal();
        String value = "178667868767867867876878678667867868768687.786767";
        factory.attribute.set(new BigDecimal(value));

        String json = ObjectMapperBuilder.build().writeValueAsString(factory);
        json=json.replace(value,"\""+value+"\"");  //long is represented as string

        ExampleBigDecimal reparsed = ObjectMapperBuilder.build().readValue(json, ExampleBigDecimal.class);
        Assertions.assertEquals(new BigDecimal(value),reparsed.attribute.get());
    }

}