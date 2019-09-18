package io.github.factoryfx.factory.attribute.primitive;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryListAttribute;
import io.github.factoryfx.factory.attribute.types.StringAttribute;
import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LongAttributeTest {

    @Test
    public void test_json(){
        LongAttribute attribute = new LongAttribute();
        long value = 1;
        attribute.set(value);
        LongAttribute copy = ObjectMapperBuilder.build().copy(attribute);
        Assertions.assertEquals(value,copy.get().longValue());
    }

    public static class ExampleLong extends FactoryBase<Void,ExampleLong> {
        public final LongAttribute attribute= new LongAttribute();
    }

    @Test
    public void test_json_from_browser(){//js can't handle large longs in number type
        ExampleLong factory = new ExampleLong();
        factory.attribute.set(Long.MAX_VALUE);

        String json = ObjectMapperBuilder.build().writeValueAsString(factory);
        json=json.replace(""+Long.MAX_VALUE,"\""+Long.MAX_VALUE+"\"");  //long is represented as string

        ExampleLong reparsed = ObjectMapperBuilder.build().readValue(json,ExampleLong.class);
        Assertions.assertEquals(Long.MAX_VALUE,reparsed.attribute.get());
    }



}