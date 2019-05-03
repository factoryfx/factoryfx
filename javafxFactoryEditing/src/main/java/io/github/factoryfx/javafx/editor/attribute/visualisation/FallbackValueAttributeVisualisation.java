package io.github.factoryfx.javafx.editor.attribute.visualisation;

import io.github.factoryfx.javafx.editor.attribute.AttributeVisualisation;
import javafx.scene.Node;
import javafx.scene.control.Label;

public class FallbackValueAttributeVisualisation implements AttributeVisualisation {

    @Override
    public Node createVisualisation() {
        return new Label("value is not editable");
    }

    @Override
    public void setReadOnly() {
        //nothing
    }

    @Override
    public void destroy() {
        //nothing
    }
}