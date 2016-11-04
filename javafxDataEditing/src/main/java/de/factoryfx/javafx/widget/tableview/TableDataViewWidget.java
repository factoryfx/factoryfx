package de.factoryfx.javafx.widget.tableview;

import de.factoryfx.data.Data;
import de.factoryfx.javafx.editor.data.DataEditor;
import de.factoryfx.javafx.util.UniformDesign;
import de.factoryfx.javafx.widget.CloseAwareWidget;
import de.factoryfx.javafx.widget.table.TableControlWidget;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class TableDataViewWidget<T extends Data> implements CloseAwareWidget {

    private final TableDataView<T> tableDataView;
    private final DataEditor dataEditor;
    private double dividerPosition = 0.333;
    private final UniformDesign uniformDesign;
    private final List<TableDataColumnSpec<T>> columns;

    public TableDataViewWidget(TableDataView<T> tableDataView, DataEditor dataEditor, UniformDesign uniformDesign) {
        this.tableDataView = tableDataView;
        this.dataEditor = dataEditor;
        this.uniformDesign = uniformDesign;
        this.columns = new ArrayList<>();
    }

    public TableDataViewWidget<T> withColumn(String columnName, Function<T,ObservableValue<String>> cellValueFactory) {
        columns.add(new TableDataColumnSpec<T>(columnName,cellValueFactory));
        return this;
    }

    public TableDataViewWidget<T> withStaticDataColumn(String columnName, Function<T,String> cellValueFactory) {
        columns.add(new TableDataColumnSpec<T>(columnName, p->new SimpleStringProperty(cellValueFactory.apply(p))));
        return this;
    }

    @Override
    public void closeNotifier() {
//        listener.changed(null, null, null);
    }

    @Override
    public Node createContent() {
//        MasterDetailPane pane = new MasterDetailPane();
        SplitPane splitPane = new SplitPane();
        splitPane.setOrientation(Orientation.VERTICAL);

        TableView<T> tableView = new TableView<>();
        tableView.setItems(tableDataView.dataList());
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        for (TableDataColumnSpec<T> col : columns) {
            TableColumn<T, String> column = new TableColumn<>(col.columnName);
            column.setCellValueFactory(param->col.cellValueProvider.apply(param.getValue()));
            tableView.getColumns().add(column);
      }

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


        TableControlWidget tableControlWidget= new TableControlWidget<>(tableView, uniformDesign);
        borderPaneWrapper.setBottom(tableControlWidget.createContent());


        return splitPane;
    }

    public TableDataViewWidget setDividerPositions(double dividerPosition) {
        this.dividerPosition = dividerPosition;
        return this;
    }
}
