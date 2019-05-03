package io.github.factoryfx.javafx.editor.attribute.builder;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.Attribute;
import io.github.factoryfx.javafx.editor.attribute.AttributeVisualisation;

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
    public AttributeVisualisation createVisualisation(Attribute<?,?> attribute, Consumer<FactoryBase<?,?>> navigateToData, FactoryBase<?,?> previousData) {
        return attributeEditorVisualisationCreator.create((A)attribute,navigateToData,previousData);
    }

    @Override
    public AttributeVisualisation createValueListVisualisation(Attribute<?,?> attribute) {
          return null;
    }

    @FunctionalInterface
    public interface AttributeEditorVisualisationCreator<A extends Attribute<?,A>> {
        AttributeVisualisation create(A attribute, Consumer<FactoryBase<?,?>> navigateToData, FactoryBase<?,?> previousData);
    }

}
