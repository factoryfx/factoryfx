package de.factoryfx.factory.merge;

import java.util.Locale;
import java.util.Optional;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.attribute.AttributeMetadata;
import de.factoryfx.factory.attribute.util.I18nAttribute;
import de.factoryfx.factory.util.VoidLiveObject;
import org.junit.Assert;
import org.junit.Test;

public class I18nMergeTest extends MergeHelperTestBase {

    public static class I18nAttributeTestPojo extends FactoryBase<VoidLiveObject,I18nAttributeTestPojo> {
        public final I18nAttribute attribute=new I18nAttribute(new AttributeMetadata());

        @Override
        protected VoidLiveObject createImp(Optional<VoidLiveObject> previousLiveObject) {
            return null;
        }
    }

    @Test
    public void test_merge_change(){
        I18nAttributeTestPojo aTest1 = new I18nAttributeTestPojo();
        aTest1.attribute.en("test1").de("test2");

        I18nAttributeTestPojo aTest2 = new I18nAttributeTestPojo();
        aTest2.attribute.en("test1X").de("test2X");

        Assert.assertTrue(merge(aTest1, aTest1, aTest2).hasNoConflicts());
        Assert.assertEquals("test1X",aTest1.attribute.get().getPreferred(Locale.ENGLISH));
        Assert.assertEquals("test2X",aTest1.attribute.get().getPreferred(Locale.GERMAN));
    }



}