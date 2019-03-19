package io.github.factoryfx.javafx.data.widget.dataview;

import io.github.factoryfx.data.Data;
import io.github.factoryfx.data.attribute.ReferenceListAttribute;
import io.github.factoryfx.javafx.data.editor.data.DataEditor;
import io.github.factoryfx.javafx.data.util.DataObservableDisplayText;
import io.github.factoryfx.javafx.data.util.UniformDesign;
import io.github.factoryfx.javafx.data.widget.Widget;
import io.github.factoryfx.javafx.data.widget.datalistedit.ReferenceListAttributeEditWidget;
import io.github.factoryfx.javafx.data.widget.table.TableControlWidget;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import java.util.List;

public class DataViewWidget<T extends Data> implements Widget {
    private final DataEditor dataEditor;
    private double dividerPosition = 0.333;
    private Orientation orientation=Orientation.HORIZONTAL;
    private final UniformDesign uniformDesign;
    private final TableView<T> tableView;
    private BorderPane listEditWidget;
    private TableColumn<T, String> column;

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
        column = new TableColumn<>("Data");
        column.setCellValueFactory(param -> new DataObservableDisplayText(param.getValue()).get());
        tableView.getColumns().add(column);

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


        listEditWidget = new BorderPane();


        TableControlWidget tableControlWidget= new TableControlWidget<>(tableView, uniformDesign);
        Node tableControlWidgetContent = tableControlWidget.createContent();
        HBox hBox = new HBox();
        hBox.getChildren().addAll(listEditWidget, tableControlWidgetContent);
        HBox.setHgrow(tableControlWidgetContent, Priority.ALWAYS);
        HBox.setMargin(tableControlWidgetContent, new Insets(0,1,0,0));
        borderPaneWrapper.setBottom(hBox);

        tableView.setTableMenuButtonVisible(false);
        return splitPane;
    }

    public void select(T data){
        tableView.getSelectionModel().select(data);
    }

    private ReferenceAttributeDataView<T, ?> dataView;//gc protection for listener

    /**
     * automatic change detection for ReferenceListAttribute, that means that changes in the attribute leads to automatic changes in the table
     * @param attribute ReferenceListAttribute
     */
    public void edit(ReferenceListAttribute<T,?> attribute){
        editReadOnly(attribute);
        listEditWidget.setCenter(new ReferenceListAttributeEditWidget<>(tableView, dataEditor::navigate, uniformDesign, attribute).createContent());
    }

    public void editReadOnly(ReferenceListAttribute<T,?> attribute){
        final Data oldData = dataEditor.editData().get();
        dataView = new ReferenceAttributeDataView<>(attribute);
        tableView.setItems(dataView.dataList());

        if (oldData!=null){
            attribute.stream().filter(d->d.idEquals(oldData)).findAny().ifPresent(dataEditor::edit);
        }

        tableView.getStyleClass().remove("hidden-tableview-headers");
        column.setText(uniformDesign.getLabelText(attribute));
    }

    public void edit(List<T> dataList){
        tableView.setItems(new UpdatableDataView<>(()->dataList).dataList());
        tableView.getStyleClass().add("hidden-tableview-headers");
    }

    public DataViewWidget setDividerPositions(double dividerPosition) {
        this.dividerPosition = dividerPosition;
        return this;
    }

    public DataViewWidget setOrientation(Orientation orientation) {
        this.orientation = orientation;
        return this;
    }

    public ReadOnlyObjectProperty<T> selectedData(){
        return tableView.getSelectionModel().selectedItemProperty();
    }
}
