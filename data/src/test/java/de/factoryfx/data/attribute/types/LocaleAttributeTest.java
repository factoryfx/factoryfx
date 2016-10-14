package de.factoryfx.data.attribute.types;

import java.util.Locale;

import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.jackson.ObjectMapperBuilder;
import org.junit.Assert;
import org.junit.Test;

public class LocaleAttributeTest {
    @Test
    public void test_json(){
        LocaleAttribute attribute= new LocaleAttribute(new AttributeMetadata()).defaultValue(Locale.FRANCE);
        LocaleAttribute copy= ObjectMapperBuilder.build().copy(attribute);
        Assert.assertEquals(Locale.FRANCE,copy.get());
    }
}