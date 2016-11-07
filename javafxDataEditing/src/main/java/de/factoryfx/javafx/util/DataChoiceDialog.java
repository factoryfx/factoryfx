package de.factoryfx.javafx.util;

import java.util.List;

import de.factoryfx.data.Data;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class DataChoiceDialog {
    public Data show(List<Data> possibleValues){
        Dialog<Data> choiceDialog = new Dialog<>();

        TableView<Data> dataTableView = new TableView<>();
        choiceDialog.getDialogPane().setContent(dataTableView);
        dataTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableColumn<Data, String> test = new TableColumn<>("Data");
        test.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().internal().getDisplayText()));
        dataTableView.getColumns().add(test);
        dataTableView.getStyleClass().add("hidden-tableview-headers");
        ObservableList<Data> items = FXCollections.observableArrayList();
        items.addAll(possibleValues);
        dataTableView.setItems(items);


        choiceDialog.getDialogPane().getStyleClass().add("choice-dialog");
        choiceDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        choiceDialog.showAndWait();
        return dataTableView.getSelectionModel().getSelectedItem();
    }
}
