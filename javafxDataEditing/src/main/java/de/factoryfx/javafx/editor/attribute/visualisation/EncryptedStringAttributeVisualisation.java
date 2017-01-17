package de.factoryfx.javafx.editor.attribute.visualisation;

import com.google.common.base.Strings;
import de.factoryfx.data.attribute.types.EncryptedStringAttribute;
import de.factoryfx.javafx.editor.attribute.ValueAttributeEditorVisualisation;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class EncryptedStringAttributeVisualisation extends ValueAttributeEditorVisualisation<String> {

    private final EncryptedStringAttribute encryptedStringAttribute;
    public EncryptedStringAttributeVisualisation(EncryptedStringAttribute encryptedStringAttribute) {
        this.encryptedStringAttribute=encryptedStringAttribute;
    }

    @Override
    public Node createContent(SimpleObjectProperty<String> boundTo) {
        TextField textField = new TextField();
        textField.textProperty().bindBidirectional(boundTo);
        textField.setEditable(false);

        TextField keyField = new TextField();
        Button keyGenButton = new Button("create new key");
        keyGenButton.setOnAction(event -> keyField.setText(encryptedStringAttribute.createKey()));

        TextField newValue = new TextField();
        newValue.disableProperty().bind(keyField.textProperty().isEmpty());
        newValue.textProperty().addListener((observable, oldValue, newValue1) -> {
            if (!Strings.isNullOrEmpty(keyField.getText())){
                encryptedStringAttribute.encrypt(newValue1,keyField.getText());
                boundTo.set(encryptedStringAttribute.get());
            }
        });


        final HBox hBox = new HBox(3);
        hBox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(textField, Priority.ALWAYS);
        hBox.getChildren().addAll(textField,new Label("key"), keyField, keyGenButton, new Label("value"), newValue);
        return hBox;
    }
}
