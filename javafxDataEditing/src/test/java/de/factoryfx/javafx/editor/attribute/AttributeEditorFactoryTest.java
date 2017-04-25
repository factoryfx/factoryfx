package de.factoryfx.javafx.editor.attribute;

import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.javafx.UniformDesignBuilder;
import org.junit.Assert;
import org.junit.Test;

public class AttributeEditorFactoryTest {

    @Test
    public void test_canEdit(){
        AttributeEditorFactory attributeEditor = new  AttributeEditorFactory(UniformDesignBuilder.build());

        Assert.assertNotNull(attributeEditor.getAttributeEditor(new StringAttribute(new AttributeMetadata()),null,null,null));
    }

}