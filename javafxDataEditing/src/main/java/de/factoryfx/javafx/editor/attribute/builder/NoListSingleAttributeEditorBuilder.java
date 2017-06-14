package de.factoryfx.javafx.editor.attribute.builder;

import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.javafx.editor.attribute.AttributeEditor;
import de.factoryfx.javafx.editor.attribute.AttributeEditorVisualisation;
import de.factoryfx.javafx.editor.data.DataEditor;
import de.factoryfx.javafx.util.UniformDesign;

import java.util.List;
import java.util.function.Function;

public class NoListSingleAttributeEditorBuilder<T,A extends Attribute<?,?>> implements SingleAttributeEditorBuilder<T> {
    private final Function<Attribute<?,?>,Boolean> isEditorFor;
    private final Function<A,AttributeEditorVisualisation<T>> attributeEditorVisualisation;
    private final UniformDesign uniformDesign;

    public NoListSingleAttributeEditorBuilder(UniformDesign uniformDesign, Function<Attribute<?,?>, Boolean> isEditorFor, Function<A,AttributeEditorVisualisation<T>> attributeEditorVisualisation) {
        this.isEditorFor = isEditorFor;
        this.attributeEditorVisualisation = attributeEditorVisualisation;
        this.uniformDesign = uniformDesign;
    }

    @Override
    public boolean isListItemEditorFor(Attribute<?,?> attribute) {
        return false;
    }

    @Override
    public boolean isEditorFor(Attribute<?,?> attribute) {
        return isEditorFor.apply(attribute);
    }

    @Override
    @SuppressWarnings("unchecked")
    public AttributeEditor<T,?> createEditor(Attribute<?,?> attribute, DataEditor dataEditor, Data previousData) {
        return new AttributeEditor((Attribute<T,?>) attribute,attributeEditorVisualisation.apply((A)attribute),uniformDesign);
    }
    @Override
    public AttributeEditor<List<T>,?> createValueListEditor(Attribute<?,?> attribute) {
        return null;
    }

}
