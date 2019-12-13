package io.github.factoryfx.factory.attribute.time;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;

public class LocalTimeAttributeTest {

    @Test
    public void test_json(){
        LocalTimeAttribute attribute = new LocalTimeAttribute();
        LocalTime value = LocalTime.now();
        attribute.set(value);
        LocalTimeAttribute copy = ObjectMapperBuilder.build().copy(attribute);
        Assertions.assertEquals(value,copy.get());
    }

    public static class ExampleLocalTimeAttribute extends FactoryBase<Void, ExampleLocalTimeAttribute> {
        public final LocalTimeAttribute attribute= new LocalTimeAttribute();
    }

    @Test
    public void test_json_from_browser(){//js can't handle large longs in number type
        ExampleLocalTimeAttribute factory = new ExampleLocalTimeAttribute();
//        String value = "12:12";
        factory.attribute.set(LocalTime.now());

        String json = ObjectMapperBuilder.build().writeValueAsString(factory);
//        System.out.println(json);
//        json=json.replace(value,"\""+value+"\"");  //long is represented as string

        ExampleLocalTimeAttribute reparsed = ObjectMapperBuilder.build().readValue(json, ExampleLocalTimeAttribute.class);
//        Assertions.assertEquals(new BigDecimal(value),reparsed.attribute.get());
    }
}