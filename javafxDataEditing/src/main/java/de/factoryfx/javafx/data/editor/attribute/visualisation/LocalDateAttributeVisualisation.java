package de.factoryfx.javafx.data.editor.attribute.visualisation;

import java.time.LocalDate;

import de.factoryfx.javafx.data.editor.attribute.ValueAttributeEditorVisualisation;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.DatePicker;

public class LocalDateAttributeVisualisation extends ValueAttributeEditorVisualisation<LocalDate> {

    @Override
    public Node createVisualisation(SimpleObjectProperty<LocalDate> boundTo, boolean readonly) {
        DatePicker datePicker = new DatePicker();
        datePicker.valueProperty().bindBidirectional(boundTo);
        datePicker.setEditable(!readonly);
        return datePicker;
    }
}
