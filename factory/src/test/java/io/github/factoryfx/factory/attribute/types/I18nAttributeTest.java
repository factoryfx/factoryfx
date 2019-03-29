package io.github.factoryfx.factory.attribute.types;

import java.util.Locale;

import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;
import io.github.factoryfx.factory.util.LanguageText;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class I18nAttributeTest {

    @Test
    public void test_json(){
        I18nAttribute attribute= new I18nAttribute().defaultValue(new LanguageText().en("eeeeeeeeeee").de("ddddddddddd"));
        I18nAttribute copy= ObjectMapperBuilder.build().copy(attribute);
        Assertions.assertEquals("eeeeeeeeeee",copy.get().internal_getPreferred(Locale.ENGLISH));
        Assertions.assertEquals("ddddddddddd",copy.get().internal_getPreferred(Locale.GERMAN));
    }

}