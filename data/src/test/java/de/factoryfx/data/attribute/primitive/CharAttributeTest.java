package de.factoryfx.data.attribute.primitive;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CharAttributeTest {

    @Test
    public void test_json(){
        CharAttribute attribute = new CharAttribute();
        char value = 'a';
        attribute.set(value);
        CharAttribute copy = ObjectMapperBuilder.build().copy(attribute);
        Assertions.assertEquals(value,copy.get().charValue());
    }

}