package io.github.factoryfx.javafx.editor.attribute.visualisation;

import io.github.factoryfx.factory.attribute.types.StringAttribute;
import io.github.factoryfx.javafx.editor.attribute.ValidationDecoration;
import io.github.factoryfx.javafx.editor.attribute.ValueAttributeVisualisation;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.WeakChangeListener;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.HTMLEditor;

/*
    wysiwyg editor
 */
public class StringHtmlAttributeVisualisation extends ValueAttributeVisualisation<String, StringAttribute> {

    private ChangeListener<String> changeListener;

    public StringHtmlAttributeVisualisation(StringAttribute attribute, ValidationDecoration validationDecoration) {
        super(attribute,validationDecoration);
    }

    @Override
    public Node createValueVisualisation() {
        HTMLEditor htmlEditor = new HTMLEditor();
        htmlEditor.disableProperty().bind(readOnly);

        changeListener = (observable, oldValue, newValue) -> htmlEditor.setHtmlText(newValue);
        observableAttributeValue.addListener(new WeakChangeListener<>(changeListener));


        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(htmlEditor);
        Button save = new Button("save");//strangely workaround HTMLEditor have no bind or change events
        save.setOnAction(event -> observableAttributeValue.set(htmlEditor.getHtmlText()));
        htmlEditor.setHtmlText(observableAttributeValue.get());
        BorderPane.setMargin(save,new Insets(3,0,3,0));
        borderPane.setTop(save);
        return borderPane;
    }


}
