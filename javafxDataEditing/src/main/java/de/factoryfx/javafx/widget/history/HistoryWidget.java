package de.factoryfx.javafx.widget.history;

import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.stream.Collectors;

import de.factoryfx.data.merge.MergeDiffInfo;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.datastorage.StoredFactoryMetadata;
import de.factoryfx.factory.log.FactoryUpdateLog;
import de.factoryfx.javafx.util.LongRunningActionExecutor;
import de.factoryfx.javafx.util.UniformDesign;
import de.factoryfx.javafx.view.factoryviewmanager.DiffDialog;
import de.factoryfx.javafx.widget.Widget;
import de.factoryfx.javafx.widget.table.TableControlWidget;
import de.factoryfx.server.rest.client.ApplicationServerRestClient;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

public class HistoryWidget<V,T extends FactoryBase<?,V>> implements Widget {

    private final UniformDesign uniformDesign;
    private final LongRunningActionExecutor longRunningActionExecutor;
    private final ApplicationServerRestClient<V, T> restClient;

    public HistoryWidget(UniformDesign uniformDesign, LongRunningActionExecutor longRunningActionExecutor, ApplicationServerRestClient<V, T> restClient) {
        this.uniformDesign=uniformDesign;
        this.longRunningActionExecutor = longRunningActionExecutor;
        this.restClient= restClient;
    }

    @Override
    public Node createContent() {
        TableView<StoredFactoryMetadata> tableView = new TableView<>();
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        final ObservableList<StoredFactoryMetadata> items = FXCollections.observableArrayList();
        tableView.setItems(items);

        final TableColumn<StoredFactoryMetadata, String> creationTimeCol = new TableColumn<>("Datum");
        creationTimeCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<StoredFactoryMetadata, String>, ObservableValue<String>>() {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<StoredFactoryMetadata, String> param) {
                return new SimpleStringProperty(param.getValue().creationTime.format(formatter));
            }
        });
        tableView.getColumns().add(creationTimeCol);

        final TableColumn<StoredFactoryMetadata, String> commentCol = new TableColumn<>("Kommentar");
        commentCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().comment));
        tableView.getColumns().add(commentCol);

        final TableColumn<StoredFactoryMetadata, String> userCol = new TableColumn<>("Benutzer");
        userCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().user));
        tableView.getColumns().add(userCol);

        longRunningActionExecutor.execute(() -> {
            final Collection<StoredFactoryMetadata> historyFactoryList = restClient.getHistoryFactoryList();
            Platform.runLater(() -> items.addAll(historyFactoryList.stream().sorted((o1, o2) -> o2.creationTime.compareTo(o1.creationTime)).collect(Collectors.toList())));
        });

        BorderPane content = new BorderPane();
        final BorderPane tableBorderPane = new BorderPane();
        tableBorderPane.setCenter(tableView);
        tableBorderPane.setBottom(new TableControlWidget<>(tableView,uniformDesign).createContent());
        content.setCenter(tableBorderPane);

        final HBox buttonPane = new HBox(3);
        buttonPane.setPadding(new Insets(3));
        final Button changesButton = new Button("Änderungen");
        changesButton.setOnAction(event -> longRunningActionExecutor.execute(() -> {
            showDiff(tableView);
        }));
        changesButton.disableProperty().bind(tableView.getSelectionModel().selectedItemProperty().isNull());
        buttonPane.getChildren().add(changesButton);

        final Button revertButton = new Button("revert to");
        revertButton.setOnAction(event -> longRunningActionExecutor.execute(() -> {
            revert(tableView);
        }));
        revertButton.disableProperty().bind(tableView.getSelectionModel().selectedItemProperty().isNull());
        buttonPane.getChildren().add(revertButton);

        tableView.setOnMouseClicked(event -> {
            if (event.getClickCount()==2 && tableView.getSelectionModel().getSelectedItem()!=null){
                showDiff(tableView);
            }
        });

        content.setBottom(buttonPane);
        return content;
    }

    private void showDiff(TableView<StoredFactoryMetadata> tableView) {
        final MergeDiffInfo diff = restClient.getDiff(tableView.getSelectionModel().getSelectedItem());
        Platform.runLater(() -> new DiffDialog().createDiffDialog(diff,uniformDesign,"Änderungen",tableView.getScene().getWindow()));
    }

    private void revert(TableView<StoredFactoryMetadata> tableView) {
        final FactoryUpdateLog factoryUpdateLog = restClient.revert(tableView.getSelectionModel().getSelectedItem());
        Platform.runLater(() -> new DiffDialog().createDiffDialog(factoryUpdateLog,uniformDesign,"Änderungen",tableView.getScene().getWindow()));
    }

}
