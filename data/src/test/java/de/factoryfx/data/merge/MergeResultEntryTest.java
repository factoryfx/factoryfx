package de.factoryfx.data.merge;

import java.util.Locale;

import de.factoryfx.data.attribute.AttributeJsonWrapper;
import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.data.jackson.ObjectMapperBuilder;
import org.junit.Assert;
import org.junit.Test;

public class MergeResultEntryTest {


    @Test
    public void json_serialisable(){
        AttributeDiffInfo mergeResultEntry = new AttributeDiffInfo(
                new AttributeJsonWrapper(new StringAttribute().en("sfdsf"),""),
                new AttributeJsonWrapper(new StringAttribute(),""), "","");
        AttributeDiffInfo copy = ObjectMapperBuilder.build().copy(mergeResultEntry);//Test json serialisation
        Assert.assertEquals("sfdsf",copy.createPreviousAttribute().internal_getPreferredLabelText(Locale.ENGLISH));
    }
}