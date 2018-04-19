package de.factoryfx.javafx.editor.attribute;

import java.util.List;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.javafx.editor.attribute.builder.SingleAttributeEditorBuilder;

public class AttributeEditorBuilderFactory<V,R extends FactoryBase<?,V,R>> extends SimpleFactoryBase<AttributeEditorBuilder,V,R> {
    public final FactoryReferenceAttribute<List<SingleAttributeEditorBuilder<?>>,FactoryBase<? extends List<SingleAttributeEditorBuilder<?>>,V,R>> editorBuildersList = new FactoryReferenceAttribute<List<SingleAttributeEditorBuilder<?>>,FactoryBase<? extends List<SingleAttributeEditorBuilder<?>>,V,R>>().en("editorAssociations");

    @Override
    public AttributeEditorBuilder createImpl() {
        return new AttributeEditorBuilder(editorBuildersList.instance());
    }

}
