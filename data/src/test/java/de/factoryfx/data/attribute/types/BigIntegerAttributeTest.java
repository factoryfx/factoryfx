package de.factoryfx.data.attribute.types;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import org.junit.Assert;
import org.junit.Test;
import java.math.BigInteger;

public class BigIntegerAttributeTest {
    @Test
    public void test_json(){
        BigIntegerAttribute attribute = new BigIntegerAttribute();
        BigInteger value = BigInteger.valueOf(1);
        attribute.set(value);
        BigIntegerAttribute copy = ObjectMapperBuilder.build().copy(attribute);
        Assert.assertEquals(value,copy.get());
    }
}