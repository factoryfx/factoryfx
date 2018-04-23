package de.factoryfx.data.attribute.types;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import org.junit.Assert;
import org.junit.Test;

public class EnumAttributeTest {

    public enum TestEnum{
        FGDFGDFGDFGDDFG
    }

    @Test
    public void test_json(){
        EnumAttribute<TestEnum> attribute= new EnumAttribute<>(TestEnum.class).defaultEnum(TestEnum.FGDFGDFGDFGDDFG);
        EnumAttribute<TestEnum> copy= ObjectMapperBuilder.build().copy(attribute);
        System.out.println(ObjectMapperBuilder.build().writeValueAsString(attribute));
        Assert.assertEquals(TestEnum.FGDFGDFGDFGDDFG,copy.getEnum());
    }

}