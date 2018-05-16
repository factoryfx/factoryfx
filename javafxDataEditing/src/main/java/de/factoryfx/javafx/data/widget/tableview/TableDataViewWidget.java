package de.factoryfx.javafx.data.widget.tableview;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import de.factoryfx.data.Data;
import de.factoryfx.javafx.data.editor.data.DataEditor;
import de.factoryfx.javafx.data.util.UniformDesign;
import de.factoryfx.javafx.data.widget.Widget;
import de.factoryfx.javafx.data.widget.table.TableControlWidget;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;

public class TableDataViewWidget<T extends Data> implements Widget {

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

    public TableDataViewWidget<T> withColumn(String columnName, Function<T,ObservableValue<String>> cellValueFactory, String cssColumnClass) {
        columns.add(new TableDataColumnSpec<>(columnName,cellValueFactory,cssColumnClass));
        return this;
    }

    public TableDataViewWidget<T> withStaticDataColumn(String columnName, Function<T,String> cellValueFactory, String cssColumnClass) {
        columns.add(new TableDataColumnSpec<>(columnName, p->new SimpleStringProperty(cellValueFactory.apply(p)),cssColumnClass));
        return this;
    }

    public TableDataViewWidget<T> withColumn(String columnName, Function<T,ObservableValue<String>> cellValueFactory) {
        return withColumn(columnName,cellValueFactory,null);
    }

    public TableDataViewWidget<T> withStaticDataColumn(String columnName, Function<T,String> cellValueFactory) {
        return withStaticDataColumn(columnName,cellValueFactory,null);
    }

    @Override
    public Node createContent() {
        SplitPane splitPane = new SplitPane();
        splitPane.setOrientation(Orientation.VERTICAL);

        TableView<T> tableView = new TableView<>();
        tableView.setItems(tableDataView.dataList());
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        for (TableDataColumnSpec<T> col : columns) {
            tableView.getColumns().add(col.create());
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
