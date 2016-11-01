package de.factoryfx.javafx.editor.attribute;

import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.javafx.util.UniformDesignFactory;
import org.junit.Assert;
import org.junit.Test;

public class AttributeEditorFactoryTest {

    @Test
    public void test_canEdit(){
        AttributeEditorFactory attributeEditor = new  AttributeEditorFactory(new UniformDesignFactory<>().instance());

        Assert.assertNotNull(attributeEditor.getAttributeEditor(new StringAttribute(new AttributeMetadata()),null));
    }

}