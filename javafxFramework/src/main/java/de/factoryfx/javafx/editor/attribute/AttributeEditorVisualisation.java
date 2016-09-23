package de.factoryfx.javafx.editor.attribute;

import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;

public interface AttributeEditorVisualisation<T> {
    Node createContent(SimpleObjectProperty<T> boundTo);
}
