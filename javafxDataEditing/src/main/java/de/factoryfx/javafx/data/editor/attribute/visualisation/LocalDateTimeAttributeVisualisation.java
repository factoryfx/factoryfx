package de.factoryfx.javafx.data.editor.attribute.visualisation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

import com.google.common.base.Strings;
import de.factoryfx.javafx.data.editor.attribute.ValueAttributeEditorVisualisation;
import de.factoryfx.javafx.data.util.TypedTextFieldHelper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

public class LocalDateTimeAttributeVisualisation extends ValueAttributeEditorVisualisation<LocalDateTime> {

    @Override
    public Node createVisualisation(SimpleObjectProperty<LocalDateTime> boundTo, boolean readonly) {
        HBox controls = new HBox(3);
        controls.setOpaqueInsets(new Insets(0,3,0,3));
        DatePicker datePicker = new DatePicker();
        datePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            updateDatePart(boundTo,newValue);
        });
        TextField timeField = new TextField();
        TypedTextFieldHelper.setupLocalTimeTextField(timeField);
        timeField.textProperty().addListener((observable, oldValue, newValue) -> {
            updateTimePart(boundTo,newValue);
        });
        controls.getChildren().addAll(datePicker,timeField);
        boundTo.addListener((observable, oldValue, newValue) -> {
            updateDatePicker(datePicker,newValue);
            updateTimeField(timeField,newValue);
        });
        Optional.ofNullable(boundTo.getValue()).ifPresent(newValue-> {
            updateDatePicker(datePicker, newValue);
            updateTimeField(timeField, newValue);
        });

        datePicker.setEditable(!readonly);
        timeField.setEditable(!readonly);
        return controls;
    }

    private void updateTimeField(TextField timeField, LocalDateTime newValue) {
        String currentTime = timeField.getText();
        if (newValue == null) {
            if (!Strings.isNullOrEmpty(currentTime))
                timeField.setText("");
            return;
        }
        String newTimeValue = DateTimeFormatter.ISO_LOCAL_TIME.format(newValue);
        if (Strings.isNullOrEmpty(currentTime)) {
            timeField.setText(newTimeValue);
            return;
        }
        if (!currentTime.equals(newTimeValue)) {
            timeField.setText(newTimeValue);
        }
    }

    private void updateDatePicker(DatePicker datePicker, LocalDateTime newValue) {
        if (newValue == null) {
            if (datePicker.getValue() != null)
                datePicker.setValue(null);
            return;
        }
        LocalDate current = datePicker.getValue();
        if (current != null && current.equals(newValue.toLocalDate()))
            return;
        datePicker.setValue(newValue.toLocalDate());
    }


    private void updateTimePart(SimpleObjectProperty<LocalDateTime> boundTo, String newValue) {
        try {
            LocalTime newLocalTime = LocalTime.from(DateTimeFormatter.ISO_LOCAL_TIME.parse(newValue));
            if (boundTo.get() == null) {
                boundTo.set(LocalDateTime.of(LocalDate.now(), newLocalTime));
                return;
            }
            LocalDateTime current = boundTo.get();
            if (current.toLocalTime().equals(newLocalTime))
                return;
            boundTo.set(LocalDateTime.of(current.toLocalDate(), newLocalTime));
        } catch (DateTimeParseException ignored) {
        }
    }

    private void updateDatePart(SimpleObjectProperty<LocalDateTime> boundTo, LocalDate newValue) {
        if (boundTo.get() == null) {
            boundTo.set(LocalDateTime.of(newValue, LocalTime.of(0,0,0,0)));
            return;
        }
        LocalDateTime current = boundTo.get();
        if (current.toLocalDate().equals(newValue))
            return;
        boundTo.set(LocalDateTime.of(newValue,current.toLocalTime()));
    }
}
