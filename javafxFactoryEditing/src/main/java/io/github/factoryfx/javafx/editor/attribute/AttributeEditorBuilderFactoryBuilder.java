package io.github.factoryfx.javafx.editor.attribute;

import io.github.factoryfx.javafx.util.UniformDesign;
import io.github.factoryfx.javafx.util.UniformDesignFactory;
import io.github.factoryfx.javafx.editor.attribute.builder.AttributeVisualisationBuilder;

import java.util.function.Function;

public class AttributeEditorBuilderFactoryBuilder {

    public AttributeEditorBuilderFactory build(UniformDesignFactory uniformDesignFactory){
        AttributeEditorBuilderFactory factory = new AttributeEditorBuilderFactory();
        for (Function<UniformDesign, AttributeVisualisationBuilder> creator : AttributeVisualisationMappingBuilder.createDefaultSingleAttributeEditorBuildersFunctions()) {
            SingleAttributeEditorBuilderFactory singleAttributeEditorBuilderFactory = new SingleAttributeEditorBuilderFactory(creator);
            singleAttributeEditorBuilderFactory.uniformDesign.set(uniformDesignFactory);
            factory.editors.add(singleAttributeEditorBuilderFactory);
        }
        return factory;
    }

}
