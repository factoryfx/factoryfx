package de.factoryfx.javafx.data.editor.attribute;

import de.factoryfx.data.attribute.Attribute;
import javafx.scene.Node;

/** also see {@link ValueAttributeEditorVisualisation} **/
public interface AttributeEditorVisualisation<T> {

    void init(Attribute<T,?> boundAttribute);

    void attributeValueChanged(T newValue);

    /*the javafx visualisation**/
    Node createVisualisation();

    /**
     * readonly visualisation
     * @return javafx node
     * */
    default Node createReadOnlyVisualisation(){
        Node visualisation = createVisualisation();
        visualisation.setDisable(true);
        return visualisation;
    }

    /**set the initial state to expanded<br>
     * some attributes have expanded and compact visualisation*/
    default void expand(){
        //nothing
    }

}
