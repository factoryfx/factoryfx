package de.factoryfx.javafx.data.editor.attribute.visualisation;

import java.text.DateFormat;
import java.util.Locale;

import de.factoryfx.javafx.data.editor.attribute.ValueAttributeEditorVisualisation;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.util.StringConverter;

public class LocaleAttributeVisualisation extends ValueAttributeEditorVisualisation<Locale> {

    @Override
    public Node createVisualisation(SimpleObjectProperty<Locale> boundTo, boolean readonly) {
        ComboBox<Locale> comboBox=new ComboBox<>();
        comboBox.setConverter(new StringConverter<Locale>() {
            @Override
            public String toString(Locale locale) {
                if (locale!=null){
                    return locale.getDisplayName();
                }
                return "";
            }

            @Override
            public Locale fromString(String string) {
                return null;
            }
        });
        comboBox.getItems().addAll(DateFormat.getAvailableLocales());
        comboBox.valueProperty().bindBidirectional(boundTo);

        comboBox.setDisable(readonly);
        return comboBox;
    }
}
