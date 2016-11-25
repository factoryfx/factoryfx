package de.factoryfx.javafx.editor.attribute;

import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;

@FunctionalInterface
public interface AttributeEditorVisualisation<T> {
    /*only called once**/
    Node createContent(SimpleObjectProperty<T> boundTo);

    default void expand(){
        //nothing
    }

}
