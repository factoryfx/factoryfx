package io.github.factoryfx.javafx.editor.attribute;

import io.github.factoryfx.factory.attribute.types.StringAttribute;
import io.github.factoryfx.javafx.UniformDesignBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AttributeVisualisationBuilderTest {

    @Test
    public void test_canEdit(){
        AttributeVisualisationMappingBuilder attributeEditor = new AttributeVisualisationMappingBuilder(AttributeVisualisationMappingBuilder.createDefaultSingleAttributeEditorBuilders(UniformDesignBuilder.build()));

        Assertions.assertNotNull(attributeEditor.getAttributeVisualisation(new StringAttribute(),null,null));
    }

}