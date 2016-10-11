package de.factoryfx.javafx.editor.attribute.visualisation;

import java.time.LocalDate;

import de.factoryfx.javafx.editor.attribute.AttributeEditorVisualisation;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.DatePicker;

public class LocalDateAttributeVisualisation implements AttributeEditorVisualisation<LocalDate> {

    @Override
    public Node createContent(SimpleObjectProperty<LocalDate> boundTo) {
        DatePicker datePicker = new DatePicker();
        datePicker.valueProperty().bindBidirectional(boundTo);
        return datePicker;
    }
}
