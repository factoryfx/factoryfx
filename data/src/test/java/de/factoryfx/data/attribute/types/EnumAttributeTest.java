package de.factoryfx.data.attribute.types;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.data.util.LanguageText;
import org.junit.Assert;
import org.junit.Test;

import java.util.Locale;
import java.util.function.Function;

public class EnumAttributeTest {

    public enum TestEnum{
        FGDFGDFGDFGDDFG_AA,
        FGDFGDFGDFGDDFG_BB
    }

    @Test
    public void test_json(){
        EnumAttribute<TestEnum> attribute= new EnumAttribute<>(TestEnum.class).defaultEnum(TestEnum.FGDFGDFGDFGDDFG_AA);
        EnumAttribute<TestEnum> copy= ObjectMapperBuilder.build().copy(attribute);
        System.out.println(ObjectMapperBuilder.build().writeValueAsString(attribute));
        Assert.assertEquals(TestEnum.FGDFGDFGDFGDDFG_AA,copy.getEnum());
    }

    @Test
    public void test_enum_i18n(){
        EnumAttribute<TestEnum> attribute= new EnumAttribute<>(TestEnum.class).deEnum(TestEnum.FGDFGDFGDFGDDFG_AA,"a_de").enEnum(TestEnum.FGDFGDFGDFGDDFG_AA,"a_en");
        Assert.assertEquals("a_de",attribute.internal_enumDisplayText(TestEnum.FGDFGDFGDFGDDFG_AA, languageText -> languageText.internal_getPreferred(Locale.GERMAN)));
        Assert.assertEquals("a_en",attribute.internal_enumDisplayText(TestEnum.FGDFGDFGDFGDDFG_AA, languageText -> languageText.internal_getPreferred(Locale.ENGLISH)));
    }

    @Test
    public void test_enum_i18n_empty(){
        EnumAttribute<TestEnum> attribute= new EnumAttribute<>(TestEnum.class);
        Assert.assertEquals("FGDFGDFGDFGDDFG_AA",attribute.internal_enumDisplayText(TestEnum.FGDFGDFGDFGDDFG_AA, languageText -> languageText.internal_getPreferred(Locale.GERMAN)));
        Assert.assertEquals("FGDFGDFGDFGDDFG_AA",attribute.internal_enumDisplayText(TestEnum.FGDFGDFGDFGDDFG_AA, languageText -> languageText.internal_getPreferred(Locale.ENGLISH)));
    }
}