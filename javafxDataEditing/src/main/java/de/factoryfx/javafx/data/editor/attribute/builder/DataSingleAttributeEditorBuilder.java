package de.factoryfx.javafx.data.editor.attribute.builder;

import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.javafx.data.editor.attribute.AttributeEditor;
import de.factoryfx.javafx.data.editor.attribute.AttributeEditorVisualisation;
import de.factoryfx.javafx.data.util.UniformDesign;

import java.util.List;
import java.util.function.Consumer;
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
    public AttributeEditor<T,?> createEditor(Attribute<?,?> attribute, Consumer<Data> navigateToData, Data previousData) {
        return new AttributeEditor<>((A)attribute,attributeEditorVisualisationCreator.create((A)attribute,navigateToData,previousData),uniformDesign);
    }

    @Override
    @SuppressWarnings("unchecked")
    public AttributeEditor<List<T>,?> createValueListEditor(Attribute<?,?> attribute) {
          return null;
    }

    @FunctionalInterface
    public interface AttributeEditorVisualisationCreator<T,A extends Attribute<T,?>> {
        AttributeEditorVisualisation<T> create(A attribute, Consumer<Data> navigateToData, Data previousData);
    }

}