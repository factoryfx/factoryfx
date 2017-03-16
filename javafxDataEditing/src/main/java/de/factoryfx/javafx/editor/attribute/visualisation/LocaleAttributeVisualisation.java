package de.factoryfx.javafx.editor.attribute.visualisation;

import java.text.DateFormat;
import java.util.Locale;

import de.factoryfx.javafx.editor.attribute.ValueAttributeEditorVisualisation;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.util.StringConverter;

public class LocaleAttributeVisualisation extends ValueAttributeEditorVisualisation<Locale> {

    @Override
    public Node createContent(SimpleObjectProperty<Locale> boundTo) {
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
        comboBox.valueProperty().bindBidirectional(boundTo);
        return comboBox;
    }
}
