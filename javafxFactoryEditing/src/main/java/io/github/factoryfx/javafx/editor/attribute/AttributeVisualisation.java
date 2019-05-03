package io.github.factoryfx.javafx.editor.attribute;

import javafx.scene.Node;

/** also see {@link ValueAttributeVisualisation} **/
public interface AttributeVisualisation {


    /*the javafx visualisation**/
    Node createVisualisation();

    /**
     * readonly visualisation
     * @return javafx node
     * */
    default Node createReadOnlyVisualisation(){
        Node visualisation = createVisualisation();
        setReadOnly();
        return visualisation;
    }

    /**set the initial state to expanded<br>
     * some attributes have expanded and compact visualisation*/
    default void expand(){
        //nothing
    }

    void setReadOnly();

    void destroy();

}
