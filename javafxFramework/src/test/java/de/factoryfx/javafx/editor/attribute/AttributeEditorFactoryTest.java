package de.factoryfx.javafx.editor.attribute;

import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.util.StringAttribute;
import org.junit.Assert;
import org.junit.Test;

public class AttributeEditorFactoryTest {

    @Test
    public void test_canEdit(){
        AttributeEditorFactory attributeEditor = new  AttributeEditorFactory();

        Assert.assertNotNull(attributeEditor.getAttributeEditor(new StringAttribute(new AttributeMetadata())));
    }

}