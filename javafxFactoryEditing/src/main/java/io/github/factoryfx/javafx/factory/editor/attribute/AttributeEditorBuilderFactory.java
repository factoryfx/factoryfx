package io.github.factoryfx.javafx.factory.editor.attribute;

import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.atrribute.FactoryReferenceListAttribute;
import io.github.factoryfx.javafx.data.editor.attribute.AttributeVisualisationMappingBuilder;
import io.github.factoryfx.javafx.factory.RichClientRoot;
import io.github.factoryfx.javafx.data.editor.attribute.builder.AttributeVisualisationBuilder;

public class AttributeEditorBuilderFactory extends SimpleFactoryBase<AttributeVisualisationMappingBuilder,RichClientRoot> {
    public final FactoryReferenceListAttribute<AttributeVisualisationBuilder,SingleAttributeEditorBuilderFactory> editors = new FactoryReferenceListAttribute<>(SingleAttributeEditorBuilderFactory.class);

    @Override
    public AttributeVisualisationMappingBuilder createImpl() {
        return new AttributeVisualisationMappingBuilder(editors.instances());
    }

    public AttributeEditorBuilderFactory(){
        super();
    }


}
