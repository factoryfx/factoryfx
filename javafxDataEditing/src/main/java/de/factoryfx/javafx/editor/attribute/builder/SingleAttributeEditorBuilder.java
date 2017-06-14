package de.factoryfx.javafx.editor.attribute.builder;

import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.javafx.editor.attribute.AttributeEditor;
import de.factoryfx.javafx.editor.data.DataEditor;

import java.util.List;

public interface SingleAttributeEditorBuilder<T> {
    boolean isListItemEditorFor(Attribute<?,?> attribute);
    boolean isEditorFor(Attribute<?,?> attribute);
    AttributeEditor<T,?> createEditor(Attribute<?,?> attribute, DataEditor dataEditor, Data previousData);
    AttributeEditor<List<T>,?> createValueListEditor(Attribute<?,?> attribute);
}
