package de.factoryfx.javafx.data.editor.attribute;

import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.javafx.UniformDesignBuilder;
import de.factoryfx.javafx.data.editor.attribute.AttributeEditorBuilder;
import org.junit.Assert;
import org.junit.Test;

public class AttributeEditorBuilderTest {

    @Test
    public void test_canEdit(){
        AttributeEditorBuilder attributeEditor = new AttributeEditorBuilder(AttributeEditorBuilder.createDefaultSingleAttributeEditorBuilders(UniformDesignBuilder.build()));

        Assert.assertNotNull(attributeEditor.getAttributeEditor(new StringAttribute(),null,null));
    }

}