package io.github.factoryfx.javafx.factory.editor.attribute;

import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryListAttribute;
import io.github.factoryfx.javafx.factory.RichClientRoot;
import io.github.factoryfx.javafx.factory.editor.attribute.builder.AttributeVisualisationBuilder;

public class AttributeEditorBuilderFactory extends SimpleFactoryBase<AttributeVisualisationMappingBuilder,RichClientRoot> {
    public final FactoryListAttribute<RichClientRoot,AttributeVisualisationBuilder,SingleAttributeEditorBuilderFactory> editors = new FactoryListAttribute<>();

    @Override
    public AttributeVisualisationMappingBuilder createImpl() {
        return new AttributeVisualisationMappingBuilder(editors.instances());
    }

    public AttributeEditorBuilderFactory(){
        super();
    }


}
