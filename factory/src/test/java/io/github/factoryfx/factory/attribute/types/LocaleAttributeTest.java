package io.github.factoryfx.factory.attribute.types;

import java.util.Locale;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LocaleAttributeTest {
    @Test
    public void test_json(){
        LocaleAttribute attribute= new LocaleAttribute().defaultValue(Locale.FRANCE);
        LocaleAttribute copy= ObjectMapperBuilder.build().copy(attribute);
        Assertions.assertEquals(Locale.FRANCE,copy.get());
    }

    @Test
    public void test_copy(){
        LocaleAttribute attribute= new LocaleAttribute().defaultValue(Locale.FRANCE);
        LocaleAttribute copy= new LocaleAttribute();
        attribute.internal_copyTo(copy, 0, 0, null, null, null);
        Assertions.assertEquals(Locale.FRANCE,copy.get());
    }
}