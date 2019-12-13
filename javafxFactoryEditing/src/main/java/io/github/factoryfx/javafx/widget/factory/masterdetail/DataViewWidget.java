package io.github.factoryfx.javafx.widget.factory.masterdetail;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryListAttribute;
import io.github.factoryfx.javafx.editor.DataEditor;
import io.github.factoryfx.javafx.util.ObservableFactoryDisplayText;
import io.github.factoryfx.javafx.util.UniformDesign;
import io.github.factoryfx.javafx.widget.Widget;
import io.github.factoryfx.javafx.widget.factory.listedit.FactoryListAttributeEditWidget;
import io.github.factoryfx.javafx.widget.table.TableControlWidget;
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

/** Master detail view to edit factories, factory list in table and detail editor*/
public class DataViewWidget<RS extends FactoryBase<?,RS>,L,F extends FactoryBase<L,RS>> implements Widget {
    private final DataEditor dataEditor;
    private double dividerPosition = 0.333;
    private Orientation orientation=Orientation.HORIZONTAL;
    private final UniformDesign uniformDesign;
    private final TableView<F> tableView;
    private BorderPane listEditWidget;
    private TableColumn<F, String> column;

    public DataViewWidget(DataEditor dataEditor, UniformDesign uniformDesign, TableView<F> tableView) {
        this.dataEditor = dataEditor;
        this.uniformDesign = uniformDesign;
        this.tableView = tableView;

        column = new TableColumn<>("Data");
        column.setCellValueFactory(param -> new ObservableFactoryDisplayText(param.getValue()));

        listEditWidget = new BorderPane();
    }

    public DataViewWidget(DataEditor dataEditor, UniformDesign uniformDesign) {
        this(dataEditor,uniformDesign,new TableView<>());
    }

    @Override
    public Node createContent() {
        SplitPane splitPane = new SplitPane();
        splitPane.setOrientation(orientation);

        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
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

        TableControlWidget<F> tableControlWidget= new TableControlWidget<>(tableView, uniformDesign);
        Node tableControlWidgetContent = tableControlWidget.createContent();
        HBox hBox = new HBox();
        hBox.getChildren().addAll(listEditWidget, tableControlWidgetContent);
        HBox.setHgrow(tableControlWidgetContent, Priority.ALWAYS);
        HBox.setMargin(tableControlWidgetContent, new Insets(0,1,0,0));
        borderPaneWrapper.setBottom(hBox);

        tableView.setTableMenuButtonVisible(false);
        return splitPane;
    }

    public void select(F data){
        tableView.getSelectionModel().select(data);
    }

    private ReferenceAttributeDataView<RS,L,F> dataView;//gc protection for listener

    /**
     * automatic change detection for ReferenceListAttribute, that means that changes in the attribute leads to automatic changes in the table
     * @param attribute ReferenceListAttribute
     */
    public void edit(FactoryListAttribute<L,F> attribute){
        editReadOnly(attribute);
        listEditWidget.setCenter(new FactoryListAttributeEditWidget<>(tableView, dataEditor::navigate, uniformDesign, attribute, attribute.internal_getMetadata()).createContent());
    }

    public void editReadOnly(FactoryListAttribute<L,F> attribute){
        final FactoryBase<?,?> oldData = dataEditor.editData().get();
        dataView = new ReferenceAttributeDataView<>(attribute);
        tableView.setItems(dataView.dataList());

        if (oldData!=null){
            attribute.stream().filter(d->d.idEquals(oldData)).findAny().ifPresent(dataEditor::edit);
        }

        tableView.getStyleClass().remove("hidden-tableview-headers");
        column.setText(uniformDesign.getLabelText(attribute));
    }

    public void edit(List<F> dataList){
        tableView.setItems(new UpdatableDataView<>(()->dataList).dataList());
        tableView.getStyleClass().add("hidden-tableview-headers");
    }

    public DataViewWidget<RS,L,F> setDividerPositions(double dividerPosition) {
        this.dividerPosition = dividerPosition;
        return this;
    }

    public DataViewWidget<RS,L,F> setOrientation(Orientation orientation) {
        this.orientation = orientation;
        return this;
    }

    public ReadOnlyObjectProperty<F> selectedData(){
        return tableView.getSelectionModel().selectedItemProperty();
    }

    @Override
    public void destroy() {
        dataEditor.destroy();
    }
}
