package de.factoryfx.factory.attribute.util;

import java.util.Locale;

import de.factoryfx.factory.attribute.AttributeMetadata;
import de.factoryfx.factory.jackson.ObjectMapperBuilder;
import de.factoryfx.factory.util.LanguageText;
import org.junit.Assert;
import org.junit.Test;

public class I18nAttributeTest {

    @Test
    public void test_json(){
        I18nAttribute attribute= new I18nAttribute(new AttributeMetadata()).defaultValue(new LanguageText().en("eeeeeeeeeee").de("ddddddddddd"));
        I18nAttribute copy= ObjectMapperBuilder.build().copy(attribute);
        Assert.assertEquals("eeeeeeeeeee",copy.get().getPreferred(Locale.ENGLISH));
        Assert.assertEquals("ddddddddddd",copy.get().getPreferred(Locale.GERMAN));
    }

}