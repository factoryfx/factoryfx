package de.factoryfx.javafx.editor.attribute.visualisation;

import com.google.common.base.Strings;
import de.factoryfx.data.attribute.types.EncryptedString;
import de.factoryfx.javafx.editor.attribute.ValueAttributeEditorVisualisation;
import de.factoryfx.javafx.util.UniformDesign;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.controlsfx.glyphfont.FontAwesome;

import java.util.function.Function;
import java.util.function.Supplier;

public class EncryptedStringAttributeVisualisation extends ValueAttributeEditorVisualisation<EncryptedString> {

    private final Supplier<String> keyCreator;
    private final UniformDesign uniformDesign;
    private final Function<String,Boolean> keyValidator;
    private BooleanBinding validKey;

    public EncryptedStringAttributeVisualisation(Supplier<String> keyCreator, Function<String,Boolean> keyValidator,  UniformDesign uniformDesign) {
        this.keyCreator=keyCreator;
        this.uniformDesign = uniformDesign;
        this.keyValidator=keyValidator;
    }

    @Override
    public Node createContent(SimpleObjectProperty<EncryptedString> boundTo) {
        TextField encryptedTextField = new TextField();
        boundTo.addListener((observable, oldValue, newValue) -> encryptedTextField.setText(newValue.getEncryptedString()));
        encryptedTextField.setEditable(false);

        TextField keyField = new TextField();
        MenuButton popupButton = new MenuButton("",uniformDesign.createIcon(FontAwesome.Glyph.QUESTION));
        final CustomMenuItem customMenuItem = new CustomMenuItem();
        final VBox popup = new VBox(3);
        popup.getChildren().addAll(new Label("Field is encrypted. For editing you need the key"));
        Button keyGenButton = new Button("create a new key for new encryption");
        popup.getChildren().add(keyGenButton);
        customMenuItem.setContent(popup);
        popupButton.getItems().add(customMenuItem);
        keyGenButton.setOnAction(event -> keyField.setText(keyCreator.get()));

        TextField decryptedTextField = new TextField();
        boundTo.addListener((observable, oldValue, newValue) -> {
            if (!keyField.getText().isEmpty()){
                decryptedTextField.setText(newValue.decrypt(keyField.getText()));
            }
        });
        decryptedTextField.setEditable(false);

        TextField newValueTextField = new TextField();

        validKey = Bindings.createBooleanBinding(() -> keyValidator.apply(keyField.getText()),keyField.textProperty());

        newValueTextField.disableProperty().bind(keyField.textProperty().isEmpty().or(validKey.not()));
        newValueTextField.textProperty().addListener((observable, oldValue, newValue1) -> {
            if (!Strings.isNullOrEmpty(keyField.getText())){
                boundTo.set(new EncryptedString(newValue1,keyField.getText()));
            }
        });

        ChangeListener<Boolean> booleanChangeListener = (observable, oldValue, newValue) -> {
            if (!newValue) {
                keyField.setTooltip(new Tooltip("Key is invalid"));
                if (keyField.getStyleClass().stream().noneMatch(c -> c.equals("error")))
                    keyField.getStyleClass().add("error");
            } else {
                keyField.getStyleClass().remove("error");
            }
        };
        validKey.addListener(booleanChangeListener);
        booleanChangeListener.changed(validKey,null,validKey.get());



        final HBox hBox = new HBox(3);
        hBox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(encryptedTextField, Priority.ALWAYS);
        HBox.setHgrow(decryptedTextField, Priority.ALWAYS);
        hBox.getChildren().addAll(new Label("Encrypted"),encryptedTextField,new Label("Decrypted"),decryptedTextField,new Label("Key"), keyField, new Label("New value"), newValueTextField, popupButton);
        return hBox;
    }
}
