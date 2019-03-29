package io.github.factoryfx.javafx.factory.editor.attribute.builder;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.Attribute;
import io.github.factoryfx.javafx.factory.editor.attribute.AttributeVisualisation;

import java.util.function.Consumer;

public interface AttributeVisualisationBuilder {
    default boolean isListItemEditorFor(Attribute<?,?> attribute){
        return false;
    }
    boolean isEditorFor(Attribute<?,?> attribute);
    AttributeVisualisation createVisualisation(Attribute<?,?> attribute, Consumer<FactoryBase<?,?>> navigateToData, FactoryBase<?,?> previousData);
    default AttributeVisualisation createValueListVisualisation(Attribute<?,?> attribute) { return null;}
}
