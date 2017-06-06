package de.factoryfx.data.merge;

import java.util.Locale;

import de.factoryfx.data.attribute.AttributeJsonWrapper;
import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.data.jackson.ObjectMapperBuilder;
import org.junit.Assert;
import org.junit.Test;

public class MergeResultEntryTest {


    @Test
    public void json_serialisable(){
        AttributeDiffInfo mergeResultEntry = new AttributeDiffInfo(
                new AttributeJsonWrapper(new StringAttribute(new AttributeMetadata().en("sfdsf")),""),
                new AttributeJsonWrapper(new StringAttribute(new AttributeMetadata()),""), "","");
        AttributeDiffInfo copy = ObjectMapperBuilder.build().copy(mergeResultEntry);//Test json serialisation
        Assert.assertEquals("sfdsf",copy.createPreviousAttribute().getPreferredLabelText(Locale.ENGLISH));
    }
}