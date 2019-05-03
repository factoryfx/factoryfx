package io.github.factoryfx.javafx.editor.attribute.visualisation;

import java.text.DateFormat;
import java.util.Locale;

import io.github.factoryfx.factory.attribute.types.LocaleAttribute;
import io.github.factoryfx.javafx.editor.attribute.ValidationDecoration;
import io.github.factoryfx.javafx.editor.attribute.ValueAttributeVisualisation;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.util.StringConverter;

public class LocaleAttributeVisualisation extends ValueAttributeVisualisation<Locale, LocaleAttribute> {

    public LocaleAttributeVisualisation(LocaleAttribute localeAttribute, ValidationDecoration validationDecoration) {
        super(localeAttribute, validationDecoration);
    }

    @Override
    public Node createValueVisualisation() {
        ComboBox<Locale> comboBox=new ComboBox<>();
        comboBox.setConverter(new StringConverter<>() {
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
        comboBox.valueProperty().bindBidirectional(observableAttributeValue);
        return comboBox;
    }
}
