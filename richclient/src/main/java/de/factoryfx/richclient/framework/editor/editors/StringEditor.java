package de.factoryfx.richclient.framework.editor.editors;

import de.factoryfx.factory.attribute.StringAttribute;
import de.factoryfx.richclient.framework.editor.AttributeEditor;
import javafx.scene.Node;
import javafx.scene.control.TextField;

public class StringEditor extends AttributeEditor<String, StringAttribute> {
    public StringEditor() {
        super(String.class);
    }

    @Override
    public Node createContent() {
        TextField textField = new TextField();
        textField.textProperty().bindBidirectional(boundTo);
//        textField.disableProperty().bind(disabledProperty());
        return addValidationDecoration(textField);
    }
}
