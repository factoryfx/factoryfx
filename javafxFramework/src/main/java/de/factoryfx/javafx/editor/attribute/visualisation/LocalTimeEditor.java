package de.factoryfx.javafx.editor.attribute.visualisation;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;

import com.google.common.base.Strings;
import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.javafx.editor.attribute.AttributeEditorVisualisation;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.util.converter.LocalTimeStringConverter;

public class LocalTimeEditor implements AttributeEditorVisualisation<LocalTime> {

    @Override
    public Node createContent(SimpleObjectProperty<LocalTime> boundTo, Attribute<LocalTime> attribute) {
        TextField textField = new TextField();
        textField.setPromptText("e.g.: 12:13");
        textField.textProperty().bindBidirectional(boundTo,new LocalTimeStringConverter());

        HBox hBox = new HBox();
        hBox.setSpacing(3);
        HBox.setHgrow(textField, Priority.ALWAYS);
        hBox.getChildren().add(textField);
        hBox.getChildren().add(new Label("h"));
        ChoiceBox<Integer> hour = new ChoiceBox<>();
        for (int i=0;i<24;i++){
            hour.getItems().add(i);
        }
        hour.setMinWidth(47);
        hour.setMaxWidth(47);
        hour.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            int newMinute=0;
            if (boundTo.getValue()!=null){
                newMinute=(boundTo.getValue().getMinute());
            }
            boundTo.setValue(LocalTime.of(newValue, newMinute));
        });
        hBox.getChildren().add(hour);
        hBox.getChildren().add(new Label("m"));
        ChoiceBox<Integer> minute = new ChoiceBox<>();
        for (int i=0;i<60;i++){
            minute.getItems().add(i);
        }
        minute.setMinWidth(47);
        minute.setMaxWidth(47);
        minute.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            int newHour=0;
            if (boundTo.getValue()!=null){
                newHour=(boundTo.getValue().getHour());
            }
            boundTo.setValue(LocalTime.of(newHour, newValue));
        });
        LocalTimeStringConverter localTimeStringConverter = new LocalTimeStringConverter();
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            textField.getStyleClass().remove("error");
            LocalTime localTime=null;
            try {
                localTime= localTimeStringConverter.fromString(newValue);
            } catch (DateTimeParseException e){
                if (!Strings.isNullOrEmpty(newValue)){
                    textField.getStyleClass().add("error");
                }
            }
            if (localTime!=null){
                hour.getSelectionModel().select(localTime.getHour());
                minute.getSelectionModel().select(localTime.getMinute());
            }
        });

        hBox.getChildren().add(minute);

        return hBox;
    }
}
