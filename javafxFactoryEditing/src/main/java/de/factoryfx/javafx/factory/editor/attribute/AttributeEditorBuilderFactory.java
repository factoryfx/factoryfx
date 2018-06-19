package de.factoryfx.javafx.factory.editor.attribute;

import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceListAttribute;
import de.factoryfx.javafx.data.editor.attribute.AttributeEditorBuilder;
import de.factoryfx.javafx.data.editor.attribute.builder.SingleAttributeEditorBuilder;
import de.factoryfx.javafx.factory.RichClientRoot;

public class AttributeEditorBuilderFactory extends SimpleFactoryBase<AttributeEditorBuilder,Void,RichClientRoot> {
    public final FactoryReferenceListAttribute<SingleAttributeEditorBuilder<?>,SingleAttributeEditorBuilderFactory> editors = new FactoryReferenceListAttribute<>();

    @Override
    public AttributeEditorBuilder createImpl() {
        return new AttributeEditorBuilder(editors.instances());
    }

    public AttributeEditorBuilderFactory(){
        super();
    }


}
