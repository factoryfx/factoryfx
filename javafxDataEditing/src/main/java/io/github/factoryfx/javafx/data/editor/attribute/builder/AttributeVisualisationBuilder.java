package io.github.factoryfx.javafx.data.editor.attribute.builder;

import io.github.factoryfx.data.Data;
import io.github.factoryfx.data.attribute.Attribute;
import io.github.factoryfx.javafx.data.editor.attribute.AttributeVisualisation;

import java.util.function.Consumer;

public interface AttributeVisualisationBuilder {
    default boolean isListItemEditorFor(Attribute<?,?> attribute){
        return false;
    }
    boolean isEditorFor(Attribute<?,?> attribute);
    AttributeVisualisation createVisualisation(Attribute<?,?> attribute, Consumer<Data> navigateToData, Data previousData);
    default AttributeVisualisation createValueListVisualisation(Attribute<?,?> attribute) { return null;}
}
