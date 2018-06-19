package de.factoryfx.javafx.data.editor.attribute.builder;

import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.javafx.data.editor.attribute.AttributeEditor;

import java.util.List;
import java.util.function.Consumer;

public interface SingleAttributeEditorBuilder<T> {
    default boolean isListItemEditorFor(Attribute<?,?> attribute){
        return false;
    }
    boolean isEditorFor(Attribute<?,?> attribute);
    AttributeEditor<T,?> createEditor(Attribute<?,?> attribute, Consumer<Data> navigateToData, Data previousData);
    default AttributeEditor<List<T>,?> createValueListEditor(Attribute<?,?> attribute){
        return null;
    }
}