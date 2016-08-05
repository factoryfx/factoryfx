package de.factoryfx.richclient.framework.editor.editors;

import de.factoryfx.factory.attribute.util.BooleanAttribute;
import de.factoryfx.richclient.framework.editor.AttributeEditor;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;

public class BooleanEditor extends AttributeEditor<Boolean,BooleanAttribute>{

    public BooleanEditor() {
        super(Boolean.class);
    }

    @Override
    public Node createContent() {
        CheckBox checkBox = new CheckBox();
        checkBox.selectedProperty().bindBidirectional(boundTo);
        checkBox.disableProperty().bind(disabledProperty());
        return checkBox;
    }

}
