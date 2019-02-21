package de.factoryfx.javafx.data.editor.attribute;

import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.javafx.UniformDesignBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AttributeVisualisationBuilderTest {

    @Test
    public void test_canEdit(){
        AttributeVisualisationMappingBuilder attributeEditor = new AttributeVisualisationMappingBuilder(AttributeVisualisationMappingBuilder.createDefaultSingleAttributeEditorBuilders(UniformDesignBuilder.build()));

        Assertions.assertNotNull(attributeEditor.getAttributeVisualisation(new StringAttribute(),null,null));
    }

}