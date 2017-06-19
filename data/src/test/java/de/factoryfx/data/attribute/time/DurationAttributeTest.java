package de.factoryfx.data.attribute.time;

import de.factoryfx.data.attribute.types.EncryptedString;
import de.factoryfx.data.attribute.types.EncryptedStringAttribute;
import de.factoryfx.data.jackson.ObjectMapperBuilder;
import org.junit.Test;

import java.time.Duration;

import static org.junit.Assert.*;


public class DurationAttributeTest {

    @Test
    public void test_Json(){
        DurationAttribute attribute = new DurationAttribute();
        attribute.set(Duration.ofDays(1));
        ObjectMapperBuilder.build().copy(attribute);
    }

}