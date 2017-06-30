package de.factoryfx.data.attribute.types;

import java.util.Locale;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import org.junit.Assert;
import org.junit.Test;

public class LocaleAttributeTest {
    @Test
    public void test_json(){
        LocaleAttribute attribute= new LocaleAttribute().defaultValue(Locale.FRANCE);
        LocaleAttribute copy= ObjectMapperBuilder.build().copy(attribute);
        Assert.assertEquals(Locale.FRANCE,copy.get());
    }

    @Test
    public void test_copy(){
        LocaleAttribute attribute= new LocaleAttribute().defaultValue(Locale.FRANCE);
        LocaleAttribute copy= new LocaleAttribute();
        attribute.internal_copyTo(copy,null);
        Assert.assertEquals(Locale.FRANCE,copy.get());
    }
}