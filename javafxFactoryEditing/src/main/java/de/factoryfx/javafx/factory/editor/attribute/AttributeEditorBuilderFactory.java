package de.factoryfx.javafx.factory.editor.attribute;

import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceListAttribute;
import de.factoryfx.javafx.data.editor.attribute.AttributeVisualisationMappingBuilder;
import de.factoryfx.javafx.factory.RichClientRoot;

public class AttributeEditorBuilderFactory extends SimpleFactoryBase<AttributeVisualisationMappingBuilder,Void,RichClientRoot> {
    public final FactoryReferenceListAttribute<de.factoryfx.javafx.data.editor.attribute.builder.AttributeVisualisationBuilder,SingleAttributeEditorBuilderFactory> editors = new FactoryReferenceListAttribute<>(SingleAttributeEditorBuilderFactory.class);

    @Override
    public AttributeVisualisationMappingBuilder createImpl() {
        return new AttributeVisualisationMappingBuilder(editors.instances());
    }

    public AttributeEditorBuilderFactory(){
        super();
    }


}
