package de.factoryfx.javafx.editor.attribute.visualisation;

import com.google.common.base.Strings;
import de.factoryfx.data.attribute.types.EncryptedString;
import de.factoryfx.data.util.LanguageText;
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

    private final LanguageText keyText = new LanguageText().en("Key").de("Schlüssel");
    private final LanguageText encryptedText = new LanguageText().en("Encrypted").de("Verschlüsselt");
    private final LanguageText decryptedText = new LanguageText().en("Decrypted").de("Entschlüsselt");
    private final LanguageText newText = new LanguageText().en("New value").de("Neuer Wert");

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
    public Node createVisualisation(SimpleObjectProperty<EncryptedString> boundTo, boolean readonly) {
        TextField encryptedTextField = new TextField();
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
        ChangeListener<EncryptedString> encryptedStringChangeListener = (observable, oldValue, newValue) -> {
            if (newValue!=null){
                encryptedTextField.setText(newValue.getEncryptedString());
            }
            if (!keyField.getText().isEmpty()) {
                decryptedTextField.setText(newValue.decrypt(keyField.getText()));
            }
        };
        boundTo.addListener(encryptedStringChangeListener);
        encryptedStringChangeListener.changed(boundTo,boundTo.get(),boundTo.get());
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
        hBox.getChildren().addAll(new Label(uniformDesign.getText(encryptedText)),encryptedTextField,new Label(uniformDesign.getText(decryptedText)),decryptedTextField,new Label(uniformDesign.getText(keyText)), keyField, new Label(uniformDesign.getText(newText)), newValueTextField, popupButton);

        hBox.setDisable(readonly);
        return hBox;
    }
}
