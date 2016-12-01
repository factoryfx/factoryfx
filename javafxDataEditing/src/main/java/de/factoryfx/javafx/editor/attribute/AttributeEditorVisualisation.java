package de.factoryfx.javafx.editor.attribute;

import de.factoryfx.data.attribute.Attribute;
import javafx.scene.Node;

public interface AttributeEditorVisualisation<T> {
    //to support mutable types. Same reference doesn't mean the content didn't change e.g List

    void init(Attribute<T> boundAttribute);

    void attributeValueChanged(T newValue);
    /*only called once**/
    Node createContent();

    default void expand(){
        //nothing
    }

}
