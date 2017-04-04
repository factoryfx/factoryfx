package de.factoryfx.data.merge;

import java.util.Locale;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.data.util.LanguageText;
import org.junit.Assert;
import org.junit.Test;

public class MergeResultEntryTest {


    @Test
    public void json_serialisable(){
        AttributeDiffInfo mergeResultEntry = new AttributeDiffInfo("","", new LanguageText().en("sfdsf"), "");
        AttributeDiffInfo copy = ObjectMapperBuilder.build().copy(mergeResultEntry);//Test json serialisation
        Assert.assertEquals("sfdsf",copy.fieldDisplayText.internal_getPreferred(Locale.ENGLISH));
    }
}