package de.factoryfx.javafx.data.widget.dataview;

import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.ReferenceListAttribute;
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

import java.util.List;

public class DataViewWidget<T extends Data> implements Widget {
    private final DataEditor dataEditor;
    private double dividerPosition = 0.333;
    private Orientation orientation=Orientation.HORIZONTAL;
    private final UniformDesign uniformDesign;
    private final TableView<T> tableView;

    public DataViewWidget(DataEditor dataEditor, UniformDesign uniformDesign, TableView<T> tableView) {
        this.dataEditor = dataEditor;
        this.uniformDesign = uniformDesign;
        this.tableView = tableView;
    }

    public DataViewWidget(DataEditor dataEditor, UniformDesign uniformDesign) {
        this(dataEditor,uniformDesign,new TableView<>());
    }

    @Override
    public Node createContent() {
        SplitPane splitPane = new SplitPane();
        splitPane.setOrientation(orientation);

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
        });


        TableControlWidget tableControlWidget= new TableControlWidget<>(tableView, uniformDesign);
        borderPaneWrapper.setBottom(tableControlWidget.createContent());
        return splitPane;
    }

    public void select(T data){
        tableView.getSelectionModel().select(data);
    }

    private ReferenceAttributeDataView<T, ?> dataView;//gc protection for listener
    /**
     * automatic change detection for ReferenceListAttribute, changes in the attribute lead to automatic changes in the table
     * */
    public void edit(ReferenceListAttribute<T,?> attribute){
        final Data oldData = dataEditor.editData().get();
        dataView = new ReferenceAttributeDataView<>(attribute);
        tableView.setItems(dataView.dataList());

        if (oldData!=null){
            attribute.stream().filter(d->d.idEquals(oldData)).findAny().ifPresent(dataEditor::edit);
        }
    }

    public void edit(List<T> dataList){
        tableView.setItems(new UpdatableDataView<>(()->dataList).dataList());
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
