package io.github.factoryfx.javafx.editor.attribute;

import java.util.List;
import java.util.function.Function;

import io.github.factoryfx.javafx.editor.attribute.builder.AttributeVisualisationBuilder;
import io.github.factoryfx.javafx.util.UniformDesign;
import io.github.factoryfx.javafx.util.UniformDesignFactory;

public class AttributeEditorBuilderFactoryBuilder {

    // Order of AttributeEditorBuilders matters.
    // If you have custom visualisations for Attributes that extend FactoryListBaseAttribute or FactoryBaseAttribute for example, you need to add them before default builders
    public AttributeEditorBuilderFactory build(UniformDesignFactory uniformDesignFactory, List<SingleAttributeEditorBuilderFactory> attributeVisualisationBuilders) {
        AttributeEditorBuilderFactory factory = new AttributeEditorBuilderFactory();
        factory.editors.addAll(attributeVisualisationBuilders);
        for (Function<UniformDesign, AttributeVisualisationBuilder> creator : AttributeVisualisationMappingBuilder.createDefaultSingleAttributeEditorBuildersFunctions()) {
            SingleAttributeEditorBuilderFactory singleAttributeEditorBuilderFactory = new SingleAttributeEditorBuilderFactory(creator);
            singleAttributeEditorBuilderFactory.uniformDesign.set(uniformDesignFactory);
            factory.editors.add(singleAttributeEditorBuilderFactory);
        }
        return factory;
    }

    public AttributeEditorBuilderFactory build(UniformDesignFactory uniformDesignFactory) {
        AttributeEditorBuilderFactory factory = new AttributeEditorBuilderFactory();
        for (Function<UniformDesign, AttributeVisualisationBuilder> creator : AttributeVisualisationMappingBuilder.createDefaultSingleAttributeEditorBuildersFunctions()) {
            SingleAttributeEditorBuilderFactory singleAttributeEditorBuilderFactory = new SingleAttributeEditorBuilderFactory(creator);
            singleAttributeEditorBuilderFactory.uniformDesign.set(uniformDesignFactory);
            factory.editors.add(singleAttributeEditorBuilderFactory);
        }
        return factory;
    }

}
