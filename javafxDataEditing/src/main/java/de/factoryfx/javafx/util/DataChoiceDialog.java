package de.factoryfx.javafx.util;

import java.util.Collection;
import java.util.Optional;

import de.factoryfx.data.Data;
import de.factoryfx.data.util.LanguageText;
import de.factoryfx.javafx.widget.table.TableControlWidget;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Window;

public class DataChoiceDialog {

    private LanguageText title=new LanguageText().en("Select").de("Auswahl");

    public Optional<Data> show(Collection<? extends Data> possibleValues, Window owner, UniformDesign uniformDesign){
        Dialog<Data> choiceDialog = new Dialog<>();
        choiceDialog.setTitle(uniformDesign.getText(title));

        choiceDialog.initOwner(owner);

        TableView<Data> dataTableView = new TableView<>();
        final BorderPane content = new BorderPane();
        content.setCenter(dataTableView);
        choiceDialog.getDialogPane().setContent(content);
        dataTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableColumn<Data, String> test = new TableColumn<>("Data");
        test.setCellValueFactory(param -> param.getValue().internal().getDisplayTextObservable());
        dataTableView.getColumns().add(test);
        dataTableView.getStyleClass().add("hidden-tableview-headers");
        ObservableList<Data> items = FXCollections.observableArrayList();
        items.addAll(possibleValues);
        dataTableView.setItems(items);

        content.setBottom(new TableControlWidget<>(dataTableView,uniformDesign).createContent());

//        dataTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
//            if (newValue!=null) {
//                Platform.runLater(()-> choiceDialog.setResult(newValue));
//            }
//        });

        choiceDialog.getDialogPane().getStylesheets().add(getClass().getResource("/de/factoryfx/javafx/css/app.css").toExternalForm());
        choiceDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        choiceDialog.getDialogPane().lookupButton(ButtonType.OK).disableProperty().bind(dataTableView.getSelectionModel().selectedItemProperty().isNull());


        choiceDialog.getDialogPane().setPrefWidth(800);

        choiceDialog.setGraphic(null);

        if (choiceDialog.showAndWait().isPresent()){
            return Optional.ofNullable(dataTableView.getSelectionModel().getSelectedItem());
        } else {
            return Optional.empty();
        }
    }
}
