package de.factoryfx.javafx.editor.attribute.builder;

import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.javafx.editor.attribute.AttributeEditor;
import de.factoryfx.javafx.editor.attribute.AttributeEditorVisualisation;
import de.factoryfx.javafx.editor.data.DataEditor;
import de.factoryfx.javafx.util.UniformDesign;

import java.util.List;
import java.util.function.Predicate;

public class DataSingleAttributeEditorBuilder<T,A extends Attribute<T,A>> implements SingleAttributeEditorBuilder<T> {

    private final AttributeEditorVisualisationCreator<T,A>  attributeEditorVisualisationCreator;
    private final UniformDesign uniformDesign;
    private final Predicate<Attribute<?,?>> isEditorFor;

    public DataSingleAttributeEditorBuilder(UniformDesign uniformDesign, Predicate<Attribute<?,?>> isEditorFor, AttributeEditorVisualisationCreator<T,A> attributeEditorVisualisationCreator) {
        this.isEditorFor=isEditorFor;
        this.attributeEditorVisualisationCreator= attributeEditorVisualisationCreator;
        this.uniformDesign = uniformDesign;
    }

    @Override
    public boolean isListItemEditorFor(Attribute<?,?> attribute) {
        return false;
    }

    @Override
    public boolean isEditorFor(Attribute<?,?> attribute) {
        return isEditorFor.test(attribute);
    }

    @Override
    @SuppressWarnings("unchecked")
    public AttributeEditor<T,?> createEditor(Attribute<?,?> attribute, DataEditor dataEditor, Data previousData) {
        return new AttributeEditor<>((A)attribute,attributeEditorVisualisationCreator.create((A)attribute,dataEditor,previousData),uniformDesign);
    }

    @Override
    @SuppressWarnings("unchecked")
    public AttributeEditor<List<T>,?> createValueListEditor(Attribute<?,?> attribute) {
          return null;
    }

    @FunctionalInterface
    public interface AttributeEditorVisualisationCreator<T,A extends Attribute<T,?>> {
        AttributeEditorVisualisation<T> create(A attribute, DataEditor dataEditor, Data previousData);
    }

}
