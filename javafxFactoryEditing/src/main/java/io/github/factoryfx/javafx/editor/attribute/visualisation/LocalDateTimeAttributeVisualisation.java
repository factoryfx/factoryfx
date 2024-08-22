package io.github.factoryfx.javafx.editor.attribute.visualisation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

import com.google.common.base.Strings;

import io.github.factoryfx.factory.attribute.time.LocalDateTimeAttribute;
import io.github.factoryfx.javafx.editor.attribute.ValidationDecoration;
import io.github.factoryfx.javafx.editor.attribute.ValueAttributeVisualisation;
import io.github.factoryfx.javafx.util.TypedTextFieldHelper;

public class LocalDateTimeAttributeVisualisation extends ValueAttributeVisualisation<LocalDateTime, LocalDateTimeAttribute> {

    public LocalDateTimeAttributeVisualisation(LocalDateTimeAttribute attribute, ValidationDecoration validationDecoration) {
        super(attribute,validationDecoration);
    }


    @Override
    public Node createValueVisualisation() {
        HBox controls = new HBox(3);
        controls.setOpaqueInsets(new Insets(0, 3, 0, 3));

        DatePicker datePicker = new DatePicker();
        datePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            updateDatePart(observableAttributeValue, newValue);
        });

        TextField timeField = new TextField();
        TypedTextFieldHelper.setupLocalTimeTextField(timeField);
        timeField.textProperty().addListener((observable, oldValue, newValue) -> {
            updateTimePart(observableAttributeValue, newValue);
        });

        controls.getChildren().addAll(datePicker, timeField);
        observableAttributeValue.addListener((observable, oldValue, newValue) -> {
            updateDatePicker(datePicker, newValue);
            updateTimeField(timeField, newValue);
        });

        Optional.ofNullable(observableAttributeValue.getValue()).ifPresent(newValue -> {
            updateDatePicker(datePicker, newValue);
            updateTimeField(timeField, newValue);
        });

        datePicker.setEditable(!readOnly.get());
        timeField.setEditable(!readOnly.get());

        return controls;
    }

    private void updateTimeField(TextField timeField, LocalDateTime newValue) {
        String currentTime = timeField.getText();
        if (newValue == null) {
            if (!Strings.isNullOrEmpty(currentTime)) {timeField.setText("");}
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
            if (datePicker.getValue() != null) {datePicker.setValue(null);}
            return;
        }
        LocalDate current = datePicker.getValue();
        if (current != null && current.equals(newValue.toLocalDate())) {return;}

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
            if (current.toLocalTime().equals(newLocalTime)) {return;}

            boundTo.set(LocalDateTime.of(current.toLocalDate(), newLocalTime));
        } catch (DateTimeParseException ignored) {
        }
    }

    private void updateDatePart(SimpleObjectProperty<LocalDateTime> boundTo, LocalDate newValue) {
        LocalDateTime current = boundTo.get();
        if (newValue == null) {
            if (current != null) {boundTo.set(null);}
            return;
        }
        if (current == null) {
            boundTo.set(LocalDateTime.of(newValue, LocalTime.of(0, 0, 0, 0)));
            return;
        }
        if (current.toLocalDate().equals(newValue)) {return;}

        boundTo.set(LocalDateTime.of(newValue, current.toLocalTime()));
    }
}
