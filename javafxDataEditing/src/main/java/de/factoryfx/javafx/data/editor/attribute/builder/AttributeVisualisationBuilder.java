package de.factoryfx.javafx.data.editor.attribute.builder;

import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.javafx.data.editor.attribute.AttributeVisualisation;

import java.util.function.Consumer;

public interface AttributeVisualisationBuilder {
    default boolean isListItemEditorFor(Attribute<?,?> attribute){
        return false;
    }
    boolean isEditorFor(Attribute<?,?> attribute);
    AttributeVisualisation createVisualisation(Attribute<?,?> attribute, Consumer<Data> navigateToData, Data previousData);
    default AttributeVisualisation createValueListVisualisation(Attribute<?,?> attribute) { return null;}
}
