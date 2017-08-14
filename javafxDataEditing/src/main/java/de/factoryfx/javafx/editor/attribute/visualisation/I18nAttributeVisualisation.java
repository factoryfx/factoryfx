package de.factoryfx.javafx.editor.attribute.visualisation;

import de.factoryfx.data.util.LanguageText;
import de.factoryfx.javafx.editor.attribute.ValueAttributeEditorVisualisation;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;

import java.text.DateFormat;
import java.util.Locale;

public class I18nAttributeVisualisation extends ValueAttributeEditorVisualisation<LanguageText> {

    @Override
    public Node createVisualisation(SimpleObjectProperty<LanguageText> boundTo, boolean readonly) {
        HBox hBox = new HBox();
        TextField displayTextfiled = new TextField();
        displayTextfiled.setEditable(false);

        boundTo.addListener((observable, oldValue, newValue) -> displayTextfiled.setText(newValue.toString()));

        ComboBox<Locale> comboBox=new ComboBox<>();
        comboBox.setConverter(new StringConverter<Locale>() {
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

        TextField textfiled = new TextField();


        Button button = new Button("set");
        button.setOnAction(event -> boundTo.set(new LanguageText().en(textfiled.getText())));



        hBox.getChildren().add(button);
        hBox.getChildren().add(displayTextfiled);

        hBox.setDisable(readonly);
        return hBox;
    }
}
