package de.factoryfx.javafx.editor.attribute.visualisation;

import de.factoryfx.javafx.editor.attribute.ValueAttributeEditorVisualisation;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WeakChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.HTMLEditor;

/*
    wysiwyg editor
 */
public class StringHtmlAttributeVisualisation extends ValueAttributeEditorVisualisation<String> {

    private ChangeListener<String> changeListener;

    @Override
    public Node createVisualisation(SimpleObjectProperty<String> attributeValue, boolean readonly) {
        HTMLEditor htmlEditor = new HTMLEditor();

        changeListener = (observable, oldValue, newValue) -> htmlEditor.setHtmlText(newValue);
        attributeValue.addListener(new WeakChangeListener<>(changeListener));
        htmlEditor.setDisable(readonly);


        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(htmlEditor);
        Button save = new Button("save");//strangely workaround HTMLEditor have no bind or change events
        save.setOnAction(event -> attributeValue.set(htmlEditor.getHtmlText()));
        htmlEditor.setHtmlText(attributeValue.get());
        BorderPane.setMargin(save,new Insets(3,0,3,0));
        borderPane.setTop(save);
        return borderPane;
    }
}
