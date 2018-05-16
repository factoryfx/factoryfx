package de.factoryfx.javafx.factory.editor.attribute;

import java.util.List;

import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.javafx.data.editor.attribute.AttributeEditorBuilder;
import de.factoryfx.javafx.data.editor.attribute.builder.SingleAttributeEditorBuilder;
import de.factoryfx.javafx.factory.RichClientRoot;

public class AttributeEditorBuilderFactory extends SimpleFactoryBase<AttributeEditorBuilder,Void,RichClientRoot> {
    public final FactoryReferenceAttribute<List<SingleAttributeEditorBuilder<?>>,DefaultEditorBuildersListFactory> editorBuildersList = new FactoryReferenceAttribute<>(DefaultEditorBuildersListFactory.class).en("editorBuildersList");

    @Override
    public AttributeEditorBuilder createImpl() {
        return new AttributeEditorBuilder(editorBuildersList.instance());
    }

}
