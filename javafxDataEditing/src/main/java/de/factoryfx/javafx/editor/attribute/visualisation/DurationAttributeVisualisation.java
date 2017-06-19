package de.factoryfx.javafx.editor.attribute.visualisation;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import com.google.common.base.Strings;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

import de.factoryfx.javafx.editor.attribute.ValueAttributeEditorVisualisation;
import de.factoryfx.javafx.editor.attribute.converter.DurationStringConverter;
import de.factoryfx.javafx.util.TypedTextFieldHelper;
import javafx.scene.layout.Priority;

public class DurationAttributeVisualisation extends ValueAttributeEditorVisualisation<Duration> {

    @Override
    public Node createContent(SimpleObjectProperty<Duration> boundTo) {

        HBox hBox = new HBox(3);
        hBox.setAlignment(Pos.CENTER_LEFT);

        Label label = new Label();
        label.textProperty().bindBidirectional(boundTo, new DurationStringConverter());

        ComboBox<ChronoUnit> comboBox = new ComboBox<>();
        comboBox.setEditable(false);
        comboBox.getItems().addAll(ChronoUnit.values());

        TextField textField = new TextField();
        TypedTextFieldHelper.setupLongTextField(textField);
        textField.disableProperty().bind(comboBox.valueProperty().isNull());
        HBox.setHgrow(textField, Priority.ALWAYS);

        comboBox.valueProperty().addListener((observable, oldValue, newValue) -> setDuration(boundTo,textField,comboBox));
        textField.textProperty().addListener((observable, oldValue, newValue) -> setDuration(boundTo,textField,comboBox));

        hBox.getChildren().addAll(comboBox, textField/*, label*/);
        return hBox;
    }

    private void setDuration(SimpleObjectProperty<Duration> boundTo, TextField textField, ComboBox<ChronoUnit> comboBox) {
        if (!Strings.isNullOrEmpty(textField.getText()) &&  comboBox.getValue() != null) {
            boundTo.set(Duration.of(Long.valueOf(textField.getCharacters().toString()), comboBox.getValue()));
        }
    }
}
