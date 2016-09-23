package de.factoryfx.data.attribute.util;

import java.util.Locale;

import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.data.util.LanguageText;
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