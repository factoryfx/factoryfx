package de.factoryfx.javafx.data.editor.attribute.visualisation;

import com.google.common.base.Strings;
import de.factoryfx.data.attribute.types.EncryptedString;
import de.factoryfx.data.attribute.types.PasswordAttribute;
import de.factoryfx.data.util.LanguageText;
import de.factoryfx.javafx.data.editor.attribute.ValidationDecoration;
import de.factoryfx.javafx.data.editor.attribute.ValueAttributeVisualisation;
import de.factoryfx.javafx.data.util.UniformDesign;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.util.function.Function;

public class PasswordAttributeVisualisation extends ValueAttributeVisualisation<EncryptedString, PasswordAttribute> {

    private final LanguageText key = new LanguageText().en("Common key").de("Gemeinsamer Schlüssel");
    private final LanguageText newPw = new LanguageText().en("Password new").de("Neues Passwort");

    private final Function<String,String> hashFunction;
    private final UniformDesign uniformDesign;
    private final Function<String,Boolean> keyValidator;
    private BooleanBinding validKey;

    public PasswordAttributeVisualisation(PasswordAttribute attribute, ValidationDecoration validationDecoration, UniformDesign uniformDesign) {
        super(attribute,validationDecoration);

        this.uniformDesign = uniformDesign;

        this.hashFunction = attribute::internal_hash;
        this.keyValidator = attribute::internal_isValidKey;
    }

    @Override
    public Node createValueVisualisation() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER_LEFT);
        grid.setHgap(6);
        grid.setVgap(6);
//        grid.setPadding(new Insets(25, 25, 25, 25));


        Label passwordKeyLabel = new Label(uniformDesign.getText(key));
        grid.add(passwordKeyLabel, 0, 1);

        TextField passwordKey = new TextField();
        grid.add(passwordKey, 1, 1);

        Label passwordNewLabel = new Label(uniformDesign.getText(newPw));
        grid.add(passwordNewLabel, 0, 2);

        PasswordField passwordNew = new PasswordField();
        grid.add(passwordNew, 1, 2);

        validKey = Bindings.createBooleanBinding(() -> keyValidator.apply(passwordKey.getText()),passwordKey.textProperty());

        passwordNew.disableProperty().bind(passwordKey.textProperty().isEmpty().or(validKey.not()));
        passwordNew.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!Strings.isNullOrEmpty(passwordKey.getText())){
                observableAttributeValue.set(new EncryptedString(hashFunction.apply(newValue),passwordKey.getText()));
            }
        });
        ChangeListener<Boolean> booleanChangeListener = (observable, oldValue, newValue) -> {
            if (!newValue) {
                passwordKey.setTooltip(new Tooltip("Key is invalid"));
                if (passwordKey.getStyleClass().stream().noneMatch(c -> c.equals("error")))
                    passwordKey.getStyleClass().add("error");
            } else {
                passwordKey.setTooltip(null);
                passwordKey.getStyleClass().remove("error");
            }
        };
        validKey.addListener(booleanChangeListener);
        booleanChangeListener.changed(validKey,validKey.get(),validKey.get());
        grid.disableProperty().bind(readOnly);
        return grid;
    }
}
