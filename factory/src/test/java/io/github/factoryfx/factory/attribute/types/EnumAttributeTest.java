package io.github.factoryfx.factory.attribute.types;

import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Locale;

public class EnumAttributeTest {

    public enum TestEnum{
        FGDFGDFGDFGDDFG_AA,
        FGDFGDFGDFGDDFG_BB
    }


    @Test
    public void test_enum_i18n(){
        EnumAttribute<TestEnum> attribute= new EnumAttribute<TestEnum>().deEnum(TestEnum.FGDFGDFGDFGDDFG_AA,"a_de").enEnum(TestEnum.FGDFGDFGDFGDDFG_AA,"a_en");
        Assertions.assertEquals("a_de",attribute.internal_enumDisplayText(TestEnum.FGDFGDFGDFGDDFG_AA, languageText -> languageText.internal_getPreferred(Locale.GERMAN)));
        Assertions.assertEquals("a_en",attribute.internal_enumDisplayText(TestEnum.FGDFGDFGDFGDDFG_AA, languageText -> languageText.internal_getPreferred(Locale.ENGLISH)));
    }

    @Test
    public void test_enum_i18n_empty(){
        EnumAttribute<TestEnum> attribute= new EnumAttribute<>();
        Assertions.assertEquals("FGDFGDFGDFGDDFG_AA",attribute.internal_enumDisplayText(TestEnum.FGDFGDFGDFGDDFG_AA, languageText -> languageText.internal_getPreferred(Locale.GERMAN)));
        Assertions.assertEquals("FGDFGDFGDFGDDFG_AA",attribute.internal_enumDisplayText(TestEnum.FGDFGDFGDFGDDFG_AA, languageText -> languageText.internal_getPreferred(Locale.ENGLISH)));
    }

    public static class ExampleEnumData{
        public final EnumAttribute<TestEnum> attribute = new EnumAttribute<>();
    }

    @Test
    public void test_in_Data(){
        ExampleEnumData exampleEnumData = new ExampleEnumData();
        exampleEnumData.attribute.set(TestEnum.FGDFGDFGDFGDDFG_AA);
        ExampleEnumData copy= ObjectMapperBuilder.build().copy(exampleEnumData);
        Assertions.assertEquals(TestEnum.FGDFGDFGDFGDDFG_AA,copy.attribute.get());
    }
}