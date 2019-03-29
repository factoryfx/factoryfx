package io.github.factoryfx.javafx.factory.widget.factory.history;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import io.github.factoryfx.factory.merge.MergeDiffInfo;
import io.github.factoryfx.factory.util.LanguageText;
import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.storage.StoredDataMetadata;
import io.github.factoryfx.factory.log.FactoryUpdateLog;
import io.github.factoryfx.javafx.factory.util.LongRunningActionExecutor;
import io.github.factoryfx.javafx.factory.util.UniformDesign;
import io.github.factoryfx.javafx.factory.widget.factory.diffdialog.DiffDialogBuilder;
import io.github.factoryfx.javafx.factory.widget.Widget;
import io.github.factoryfx.javafx.factory.widget.table.TableControlWidget;
import io.github.factoryfx.microservice.rest.client.MicroserviceRestClient;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.util.Callback;
import org.controlsfx.glyphfont.FontAwesome;

public class HistoryWidget<R extends FactoryBase<?,R>,S> implements Widget {

    private LanguageText changesText= new LanguageText().en("Changes").de("Änderungen");
    private LanguageText revertText= new LanguageText().en("Revert to").de("Zurücksetzen zu");
    private LanguageText refreshText= new LanguageText().en("Refresh").de("Aktualisieren");

    private LanguageText dateText= new LanguageText().en("Date").de("Datum");
    private LanguageText commentText= new LanguageText().en("Comment").de("Kommentar");
    private LanguageText userText= new LanguageText().en("User").de("Benutzer");



    private final UniformDesign uniformDesign;
    private final LongRunningActionExecutor longRunningActionExecutor;
    private final MicroserviceRestClient<R,S> restClient;
    private Consumer<List<StoredDataMetadata>> tableUpdater;
    private final DiffDialogBuilder diffDialogBuilder;
    private BooleanBinding firstVersionSelected;

    public HistoryWidget(UniformDesign uniformDesign, LongRunningActionExecutor longRunningActionExecutor, MicroserviceRestClient<R, S> restClient, DiffDialogBuilder diffDialogBuilder) {
        this.uniformDesign=uniformDesign;
        this.longRunningActionExecutor = longRunningActionExecutor;
        this.restClient= restClient;
        this.diffDialogBuilder = diffDialogBuilder;
    }


    @Override
    public Node createContent() {
        TableView<StoredDataMetadata> tableView = new TableView<>();
        tableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        final ObservableList<StoredDataMetadata> items = FXCollections.observableArrayList();
        tableView.setItems(items);

        final TableColumn<StoredDataMetadata, String> creationTimeCol = new TableColumn<>(uniformDesign.getText(dateText));
        creationTimeCol.setCellValueFactory(new Callback<>() {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<StoredDataMetadata, String> param) {
                return new SimpleStringProperty(param.getValue().creationTime.format(formatter));
            }
        });
        tableView.getColumns().add(creationTimeCol);

        final TableColumn<StoredDataMetadata, String> commentCol = new TableColumn<>(uniformDesign.getText(commentText));
        commentCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().comment));
        tableView.getColumns().add(commentCol);

        final TableColumn<StoredDataMetadata, String> userCol = new TableColumn<>(uniformDesign.getText(userText));
        userCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().user));
        tableView.getColumns().add(userCol);


        tableUpdater = items::setAll;
        update();

        BorderPane content = new BorderPane();
        final BorderPane tableBorderPane = new BorderPane();
        tableBorderPane.setCenter(tableView);
        tableBorderPane.setBottom(new TableControlWidget<>(tableView,uniformDesign).createContent());
        content.setCenter(tableBorderPane);

        final HBox buttonPane = new HBox(3);
        buttonPane.setPadding(new Insets(3));

        firstVersionSelected = Bindings.createBooleanBinding(() -> isFirstVersion(tableView), items, tableView.getSelectionModel().selectedItemProperty());

        final Button changesButton = new Button(uniformDesign.getText(changesText));
        changesButton.setOnAction(event -> longRunningActionExecutor.execute(() -> showDiff(tableView)));
        changesButton.disableProperty().bind(tableView.getSelectionModel().selectedItemProperty().isNull().or(firstVersionSelected));
        buttonPane.getChildren().add(changesButton);
        uniformDesign.addIcon(changesButton, FontAwesome.Glyph.EXCHANGE);

        final Button revertButton = new Button(uniformDesign.getText(revertText));
        revertButton.setOnAction(event -> longRunningActionExecutor.execute(() -> revert(tableView)));
        revertButton.disableProperty().bind(tableView.getSelectionModel().selectedItemProperty().isNull().or(firstVersionSelected));
        buttonPane.getChildren().add(revertButton);
        uniformDesign.addIcon(revertButton, FontAwesome.Glyph.ROTATE_LEFT);

        HBox buttonPaneRight = new HBox(3);
        HBox.setHgrow(buttonPaneRight, Priority.ALWAYS);
        buttonPane.getChildren().add(buttonPaneRight);
        final Button refreshButton = new Button(uniformDesign.getText(refreshText));
        refreshButton.setOnAction(event -> longRunningActionExecutor.execute(this::update));
        buttonPaneRight.setAlignment(Pos.CENTER_RIGHT);
        buttonPaneRight.getChildren().add(refreshButton);
        uniformDesign.addIcon(refreshButton, FontAwesome.Glyph.REFRESH);

        tableView.setOnMouseClicked(event -> {
            if (event.getClickCount()==2 && tableView.getSelectionModel().getSelectedItem()!=null){
                showDiff(tableView);
            }
        });

        content.setBottom(buttonPane);
        return content;
    }

    private void update() {
        longRunningActionExecutor.execute(() -> {
            final Collection<StoredDataMetadata<S>> historyFactoryList = restClient.getHistoryFactoryList();
            Platform.runLater(() -> tableUpdater.accept(historyFactoryList.stream().sorted((o1, o2) -> o2.creationTime.compareTo(o1.creationTime)).collect(Collectors.toList())));
        });
    }

    @SuppressWarnings("unchecked")
    private void showDiff(TableView<StoredDataMetadata> tableView) {
        if (!isFirstVersion(tableView)){
            final MergeDiffInfo<R> diff = restClient.getDiff(tableView.getSelectionModel().getSelectedItem());
            Platform.runLater(() -> diffDialogBuilder.createDiffDialog(diff,uniformDesign.getText(changesText),tableView.getScene().getWindow()));
        }
    }

    @SuppressWarnings("unchecked")
    private void revert(TableView<StoredDataMetadata> tableView) {
        final FactoryUpdateLog factoryUpdateLog = restClient.revert(tableView.getSelectionModel().getSelectedItem());
        Platform.runLater(() -> diffDialogBuilder.createDiffDialog(factoryUpdateLog,uniformDesign.getText(changesText),tableView.getScene().getWindow()));
        update();
    }

    public boolean isFirstVersion(TableView<StoredDataMetadata> tableView){
        List<StoredDataMetadata> list = new ArrayList<>(tableView.getItems());
        if (list.size()==0){
            return false;
        }
        if (list.size()==1){
            return true;
        }
        list.sort(Comparator.comparing(o -> o.creationTime));
        return tableView.getSelectionModel().getSelectedItem()==list.get(0);
    }

}
