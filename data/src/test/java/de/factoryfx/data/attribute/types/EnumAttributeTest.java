package de.factoryfx.data.attribute.types;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import org.junit.Assert;
import org.junit.Test;

import java.util.Locale;

public class EnumAttributeTest {

    public enum TestEnum{
        FGDFGDFGDFGDDFG_AA,
        FGDFGDFGDFGDDFG_BB
    }

    @Test
    public void test_json_class(){
        ExampleEnumData data= new ExampleEnumData();
        ExampleEnumData copy= ObjectMapperBuilder.build().copy(data);
        Assert.assertEquals(2,copy.attribute.internal_possibleEnumValues().size());
    }

    @Test
    public void test_json_getAttributeType(){
        ExampleEnumData data= new ExampleEnumData();
        ExampleEnumData copy= ObjectMapperBuilder.build().copy(data);
        Assert.assertEquals(TestEnum.class,copy.attribute.internal_getAttributeType().dataType);
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

    public static class ExampleEnumData{
        public final EnumAttribute<TestEnum> attribute = new EnumAttribute<>(TestEnum.class);
    }

    @Test
    public void test_in_Data(){
        ExampleEnumData exampleEnumData = new ExampleEnumData();
        exampleEnumData.attribute.set(TestEnum.FGDFGDFGDFGDDFG_AA);
        ExampleEnumData copy= ObjectMapperBuilder.build().copy(exampleEnumData);
        Assert.assertEquals(TestEnum.FGDFGDFGDFGDDFG_AA,copy.attribute.get());
    }
}