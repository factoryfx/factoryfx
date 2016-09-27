package de.factoryfx.javafx.editor.attribute;

import de.factoryfx.data.attribute.Attribute;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;

@FunctionalInterface
public interface AttributeEditorVisualisation<T> {
    /*only called once**/
    Node createContent(SimpleObjectProperty<T> boundTo, Attribute<T> attribute);
}
