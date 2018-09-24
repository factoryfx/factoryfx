package de.factoryfx.javafx.data.editor.attribute.builder;

import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.javafx.data.editor.attribute.AttributeVisualisation;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class SimpleAttributeVisualisationBuilder<A extends Attribute<?,A>> implements AttributeVisualisationBuilder {

    private final AttributeEditorVisualisationCreator<A>  attributeEditorVisualisationCreator;
    private final Predicate<Attribute<?,?>> isEditorFor;

    public SimpleAttributeVisualisationBuilder(Predicate<Attribute<?,?>> isEditorFor, AttributeEditorVisualisationCreator<A> attributeEditorVisualisationCreator) {
        this.isEditorFor=isEditorFor;
        this.attributeEditorVisualisationCreator= attributeEditorVisualisationCreator;
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
    public AttributeVisualisation createVisualisation(Attribute<?,?> attribute, Consumer<Data> navigateToData, Data previousData) {
        return attributeEditorVisualisationCreator.create((A)attribute,navigateToData,previousData);
    }

    @Override
    @SuppressWarnings("unchecked")
    public AttributeVisualisation createValueListVisualisation(Attribute<?,?> attribute) {
          return null;
    }

    @FunctionalInterface
    public interface AttributeEditorVisualisationCreator<A extends Attribute<?,A>> {
        AttributeVisualisation create(A attribute, Consumer<Data> navigateToData, Data previousData);
    }

}
