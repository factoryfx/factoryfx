package de.factoryfx.data.merge;

import java.util.Locale;

import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.types.I18nAttribute;
import org.junit.Assert;
import org.junit.Test;

public class I18nMergeTest extends MergeHelperTestBase {

    public static class I18nAttributeTestPojo extends Data {
        public final I18nAttribute attribute=new I18nAttribute();
    }

    @Test
    public void test_merge_change(){
        I18nAttributeTestPojo aTest1 = new I18nAttributeTestPojo();
        aTest1.attribute.en("test1").de("test2");
        aTest1=aTest1.internal().prepareUsableCopy();

        I18nAttributeTestPojo aTest2 = new I18nAttributeTestPojo();
        aTest2.attribute.en("test1X").de("test2X");
        aTest2=aTest2.internal().prepareUsableCopy();

        Assert.assertTrue(merge(aTest1, aTest1, aTest2).hasNoConflicts());
        Assert.assertEquals("test1X",aTest1.attribute.get().internal_getPreferred(Locale.ENGLISH));
        Assert.assertEquals("test2X",aTest1.attribute.get().internal_getPreferred(Locale.GERMAN));
    }



}