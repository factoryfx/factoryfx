package de.factoryfx.data.attribute.types;

import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.data.merge.testfactories.IdData;
import org.junit.Assert;
import org.junit.Test;

public class TableAttributeTest {
    @Test
    public void test_json(){
        TableAttribute attribute= new TableAttribute(new AttributeMetadata()).defaultValue(new TableAttribute.Table().addRow().addColumn().setCellValue(0,0,"test"));
        TableAttribute copy= ObjectMapperBuilder.build().copy(attribute);
        Assert.assertEquals("test",copy.get().getCellValue(0,0));
    }

    public static class TestTableAttributeData extends IdData {
        public final TableAttribute attribute= new TableAttribute(new AttributeMetadata()).defaultValue(new TableAttribute.Table().addRow().addColumn().setCellValue(0,0,"test"));
    }

    @Test
    public void test_copy(){
        TestTableAttributeData org = new TestTableAttributeData();
        TestTableAttributeData copy = org.copy();
        Assert.assertNotEquals(org.attribute.get(),copy.attribute.get());
    }
}