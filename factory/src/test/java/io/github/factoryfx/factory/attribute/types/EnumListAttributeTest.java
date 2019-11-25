package io.github.factoryfx.factory.attribute.types;

import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Locale;

public class EnumListAttributeTest {
    public enum TestEnum{
        FGDFGDFGDFGDDFG_AA,
        FGDFGDFGDFGDDFG_BB
    }

    public static class ExampleEnumListData{
        public final EnumListAttribute<TestEnum> attribute = new EnumListAttribute<>();
    }

    @Test
    public void test_in_Data(){
        ExampleEnumListData exampleEnumData = new ExampleEnumListData();
        exampleEnumData.attribute.add(TestEnum.FGDFGDFGDFGDDFG_AA);
        ExampleEnumListData copy= ObjectMapperBuilder.build().copy(exampleEnumData);
        Assertions.assertEquals(TestEnum.FGDFGDFGDFGDDFG_AA,copy.attribute.get().get(0));
    }

    @Test
    public void test_enum_i18n(){
        EnumListAttribute<TestEnum> attribute= new EnumListAttribute<TestEnum>().deEnum(TestEnum.FGDFGDFGDFGDDFG_AA,"a_de").enEnum(TestEnum.FGDFGDFGDFGDDFG_AA,"a_en");
        Assertions.assertEquals("a_de",attribute.internal_enumDisplayText(TestEnum.FGDFGDFGDFGDDFG_AA, languageText -> languageText.internal_getPreferred(Locale.GERMAN)));
        Assertions.assertEquals("a_en",attribute.internal_enumDisplayText(TestEnum.FGDFGDFGDFGDDFG_AA, languageText -> languageText.internal_getPreferred(Locale.ENGLISH)));
    }

    @Test
    public void test_enum_i18n_empty(){
        EnumListAttribute<TestEnum> attribute= new EnumListAttribute<TestEnum>();
        Assertions.assertEquals("FGDFGDFGDFGDDFG_AA",attribute.internal_enumDisplayText(TestEnum.FGDFGDFGDFGDDFG_AA, languageText -> languageText.internal_getPreferred(Locale.GERMAN)));
        Assertions.assertEquals("FGDFGDFGDFGDDFG_AA",attribute.internal_enumDisplayText(TestEnum.FGDFGDFGDFGDDFG_AA, languageText -> languageText.internal_getPreferred(Locale.ENGLISH)));
    }
}