package de.factoryfx.javafx.data.editor.attribute.visualisation;

import java.time.LocalDate;

import de.factoryfx.data.attribute.time.LocalDateAttribute;
import de.factoryfx.javafx.data.editor.attribute.ValidationDecoration;
import de.factoryfx.javafx.data.editor.attribute.ValueAttributeVisualisation;
import javafx.scene.Node;
import javafx.scene.control.DatePicker;

public class LocalDateAttributeVisualisation extends ValueAttributeVisualisation<LocalDate, LocalDateAttribute> {

    public LocalDateAttributeVisualisation(LocalDateAttribute attribute, ValidationDecoration validationDecoration) {
        super(attribute,validationDecoration);
    }

    @Override
    public Node createValueVisualisation() {
        DatePicker datePicker = new DatePicker();
        datePicker.valueProperty().bindBidirectional(observableAttributeValue);
        datePicker.disableProperty().bind(readOnly);
        return datePicker;
    }
}
