package de.factoryfx.javafx.widget.dataview;

import de.factoryfx.data.Data;
import de.factoryfx.javafx.editor.data.DataEditor;
import de.factoryfx.javafx.util.UniformDesign;
import de.factoryfx.javafx.widget.CloseAwareWidget;
import de.factoryfx.javafx.widget.table.TableControlWidget;
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
    private final UniformDesign uniformDesign;
    private final TableView<Data> tableView;

    public DataViewWidget(DataView dataView, DataEditor dataEditor, UniformDesign uniformDesign, TableView<Data> tableView) {
        this.dataView = dataView;
        this.dataEditor = dataEditor;
        this.uniformDesign = uniformDesign;
        this.tableView = tableView;
    }

    public DataViewWidget(DataView dataView, DataEditor dataEditor, UniformDesign uniformDesign) {
        this(dataView,dataEditor,uniformDesign,new TableView<>());
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

        tableView.setItems(dataView.dataList());
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableColumn<Data, String> test = new TableColumn<>("Data");
        test.setCellValueFactory(param -> param.getValue().internal().getDisplayTextObservable());
        tableView.getColumns().add(test);
        tableView.getStyleClass().add("hidden-tableview-headers");

        BorderPane borderPaneWrapper = new BorderPane();
        borderPaneWrapper.setCenter(tableView);
        SplitPane.setResizableWithParent(borderPaneWrapper, Boolean.FALSE);
        splitPane.getItems().add(borderPaneWrapper);

        Node dataEditorWidget = this.dataEditor.createContent();
        SplitPane.setResizableWithParent(dataEditorWidget, Boolean.TRUE);
        splitPane.getItems().add(dataEditorWidget);
        splitPane.setDividerPositions(dividerPosition);

        dataEditor.reset();
        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            dataEditor.edit(newValue);
            dataEditor.resetHistory();
        });


        TableControlWidget tableControlWidget= new TableControlWidget<>(tableView, uniformDesign);
        borderPaneWrapper.setBottom(tableControlWidget.createContent());




        return splitPane;
    }

    public DataViewWidget setDividerPositions(double dividerPosition) {
        this.dividerPosition = dividerPosition;
        return this;
    }
}
