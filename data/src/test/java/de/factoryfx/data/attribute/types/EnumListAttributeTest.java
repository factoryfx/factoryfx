package de.factoryfx.data.attribute.types;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import org.junit.Assert;
import org.junit.Test;

import java.util.Locale;

import static org.junit.Assert.*;

public class EnumListAttributeTest {
    public enum TestEnum{
        FGDFGDFGDFGDDFG_AA,
        FGDFGDFGDFGDDFG_BB
    }

    public static class ExampleEnumListData{
        public final EnumListAttribute<TestEnum> attribute = new EnumListAttribute<>(TestEnum.class);
    }

    @Test
    public void test_json_class(){
        ExampleEnumListData data= new ExampleEnumListData();
        ExampleEnumListData copy= ObjectMapperBuilder.build().copy(data);
        Assert.assertEquals(2,copy.attribute.internal_possibleEnumValues().size());
    }

    @Test
    public void test_json_getAttributeType(){
        ExampleEnumListData data= new ExampleEnumListData();
        ExampleEnumListData copy= ObjectMapperBuilder.build().copy(data);
        Assert.assertEquals(TestEnum.class,copy.attribute.internal_getAttributeType().dataType);
    }

    @Test
    public void test_in_Data(){
        ExampleEnumListData exampleEnumData = new ExampleEnumListData();
        exampleEnumData.attribute.add(TestEnum.FGDFGDFGDFGDDFG_AA);
        ExampleEnumListData copy= ObjectMapperBuilder.build().copy(exampleEnumData);
        Assert.assertEquals(TestEnum.FGDFGDFGDFGDDFG_AA,copy.attribute.get().get(0));
    }

    @Test
    public void test_enum_i18n(){
        EnumListAttribute<TestEnum> attribute= new EnumListAttribute<>(TestEnum.class).deEnum(TestEnum.FGDFGDFGDFGDDFG_AA,"a_de").enEnum(TestEnum.FGDFGDFGDFGDDFG_AA,"a_en");
        Assert.assertEquals("a_de",attribute.internal_enumDisplayText(TestEnum.FGDFGDFGDFGDDFG_AA, languageText -> languageText.internal_getPreferred(Locale.GERMAN)));
        Assert.assertEquals("a_en",attribute.internal_enumDisplayText(TestEnum.FGDFGDFGDFGDDFG_AA, languageText -> languageText.internal_getPreferred(Locale.ENGLISH)));
    }

    @Test
    public void test_enum_i18n_empty(){
        EnumListAttribute<TestEnum> attribute= new EnumListAttribute<>(TestEnum.class);
        Assert.assertEquals("FGDFGDFGDFGDDFG_AA",attribute.internal_enumDisplayText(TestEnum.FGDFGDFGDFGDDFG_AA, languageText -> languageText.internal_getPreferred(Locale.GERMAN)));
        Assert.assertEquals("FGDFGDFGDFGDDFG_AA",attribute.internal_enumDisplayText(TestEnum.FGDFGDFGDFGDDFG_AA, languageText -> languageText.internal_getPreferred(Locale.ENGLISH)));
    }
}