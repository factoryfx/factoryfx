package io.github.factoryfx.javafx.data.editor.attribute.visualisation;

import io.github.factoryfx.data.attribute.types.I18nAttribute;
import io.github.factoryfx.data.util.LanguageText;
import io.github.factoryfx.javafx.data.editor.attribute.ValidationDecoration;
import io.github.factoryfx.javafx.data.editor.attribute.ValueAttributeVisualisation;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;

import java.text.DateFormat;
import java.util.Locale;

public class I18nAttributeVisualisation extends ValueAttributeVisualisation<LanguageText, I18nAttribute> {

    public I18nAttributeVisualisation(I18nAttribute attribute, ValidationDecoration validationDecoration) {
        super(attribute,validationDecoration);
    }

    @Override
    public Node createValueVisualisation() {
        HBox hBox = new HBox();
        TextField displayTextfiled = new TextField();
        displayTextfiled.setEditable(false);

        observableAttributeValue.addListener((observable, oldValue, newValue) -> displayTextfiled.setText(newValue.toString()));

        ComboBox<Locale> comboBox=new ComboBox<>();
        comboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Locale locale) {
                return locale.getDisplayName();
            }

            @Override
            public Locale fromString(String string) {
                return null;
            }
        });
        comboBox.getItems().addAll(DateFormat.getAvailableLocales());

        TextField textfield = new TextField();


        Button button = new Button("set");
        button.setOnAction(event -> observableAttributeValue.set(new LanguageText().en(textfield.getText())));



        hBox.getChildren().add(button);
        hBox.getChildren().add(displayTextfiled);
        hBox.disableProperty().bind(readOnly);
        return hBox;
    }
}
