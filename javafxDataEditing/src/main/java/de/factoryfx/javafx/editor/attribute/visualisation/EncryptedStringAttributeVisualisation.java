package de.factoryfx.javafx.editor.attribute.visualisation;

import com.google.common.base.Strings;
import de.factoryfx.data.attribute.types.EncryptedStringAttribute;
import de.factoryfx.javafx.editor.attribute.ValueAttributeEditorVisualisation;
import de.factoryfx.javafx.util.UniformDesign;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.controlsfx.glyphfont.FontAwesome;

public class EncryptedStringAttributeVisualisation extends ValueAttributeEditorVisualisation<String> {

    private final EncryptedStringAttribute encryptedStringAttribute;
    private final UniformDesign uniformDesign;
    public EncryptedStringAttributeVisualisation(EncryptedStringAttribute encryptedStringAttribute, UniformDesign uniformDesign) {
        this.encryptedStringAttribute=encryptedStringAttribute;
        this.uniformDesign = uniformDesign;
    }

    @Override
    public Node createContent(SimpleObjectProperty<String> boundTo) {
        TextField textField = new TextField();
        textField.textProperty().bindBidirectional(boundTo);
        textField.setEditable(false);

        TextField keyField = new TextField();
        MenuButton popupButton = new MenuButton("",uniformDesign.createIcon(FontAwesome.Glyph.QUESTION));
        final CustomMenuItem customMenuItem = new CustomMenuItem();
        final VBox popup = new VBox(3);
        popup.getChildren().addAll(new Label("Field is encrypted. For editing you need the key"));
        Button keyGenButton = new Button("create a new key");
        popup.getChildren().add(keyGenButton);
        customMenuItem.setContent(popup);
        popupButton.getItems().add(customMenuItem);
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
        hBox.getChildren().addAll(textField,new Label("key"), keyField, new Label("value"), newValue, popupButton);
        return hBox;
    }
}
