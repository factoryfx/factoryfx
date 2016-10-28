package de.factoryfx.javafx.widget.dataview;

import de.factoryfx.data.Data;
import de.factoryfx.javafx.editor.data.DataEditor;
import de.factoryfx.javafx.widget.CloseAwareWidget;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;

public class DataViewWidget implements CloseAwareWidget {
    private final DataView dataView;
    private final DataEditor dataEditor;
    private double dividerPosition = 0.333;

    public DataViewWidget(DataView dataView, DataEditor dataEditor) {
        this.dataView = dataView;
        this.dataEditor = dataEditor;
    }

    @Override
    public void closeNotifier() {
//        listener.changed(null, null, null);
    }

    @Override
    public Node createContent() {
//        MasterDetailPane pane = new MasterDetailPane();
        SplitPane splitPane = new SplitPane();
        splitPane.setOrientation(Orientation.HORIZONTAL);

        TableView<Data> tableView = new TableView<>();
        tableView.setItems(dataView.dataList());
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableColumn<Data, String> test = new TableColumn<>("Data");
        test.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getDisplayText()));
        tableView.getColumns().add(test);
        tableView.getStyleClass().add("hidden-tableview-headers");

        BorderPane borderPaneWrapper = new BorderPane();
        borderPaneWrapper.setCenter(tableView);
        splitPane.getItems().add(borderPaneWrapper);

        Node dataEditorWidget = this.dataEditor.createContent();
        SplitPane.setResizableWithParent(dataEditorWidget, Boolean.FALSE);
        splitPane.getItems().add(dataEditorWidget);
        splitPane.setDividerPositions(dividerPosition);

        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            dataEditor.edit(newValue);
            dataEditor.resetHistory();
        });

        return splitPane;
    }

    public DataViewWidget setDividerPositions(double dividerPosition) {
        this.dividerPosition = dividerPosition;
        return this;
    }
}
