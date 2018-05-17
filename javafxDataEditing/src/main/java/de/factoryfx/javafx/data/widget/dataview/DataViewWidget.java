package de.factoryfx.javafx.data.widget.dataview;

import de.factoryfx.data.Data;
import de.factoryfx.javafx.data.editor.data.DataEditor;
import de.factoryfx.javafx.data.util.DataObservableDisplayText;
import de.factoryfx.javafx.data.util.UniformDesign;
import de.factoryfx.javafx.data.widget.Widget;
import de.factoryfx.javafx.data.widget.table.TableControlWidget;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;

public class DataViewWidget<T extends Data> implements Widget {
    private final DataView<T> dataView;
    private final DataEditor dataEditor;
    private double dividerPosition = 0.333;
    private Orientation orientation=Orientation.HORIZONTAL;
    private final UniformDesign uniformDesign;
    private final TableView<T> tableView;

    public DataViewWidget(DataView<T> dataView, DataEditor dataEditor, UniformDesign uniformDesign, TableView<T> tableView) {
        this.dataView = dataView;
        this.dataEditor = dataEditor;
        this.uniformDesign = uniformDesign;
        this.tableView = tableView;
    }

    public DataViewWidget(DataView<T> dataView, DataEditor dataEditor, UniformDesign uniformDesign) {
        this(dataView,dataEditor,uniformDesign,new TableView<>());
    }

    @Override
    public Node createContent() {
        dataEditor.reset();
        SplitPane splitPane = new SplitPane();
        splitPane.setOrientation(orientation);

        tableView.setItems(dataView.dataList());
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableColumn<T, String> test = new TableColumn<>("Data");
        test.setCellValueFactory(param -> new DataObservableDisplayText(param.getValue()).get());
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

        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            dataEditor.edit(newValue);
            dataEditor.resetHistory();
        });


        TableControlWidget tableControlWidget= new TableControlWidget<>(tableView, uniformDesign);
        borderPaneWrapper.setBottom(tableControlWidget.createContent());




        return splitPane;
    }

    public void edit(T data){
        tableView.getSelectionModel().select(data);
    }

    public DataViewWidget setDividerPositions(double dividerPosition) {
        this.dividerPosition = dividerPosition;
        return this;
    }

    public DataViewWidget setOrientation(Orientation orientation) {
        this.orientation = orientation;
        return this;
    }
}
