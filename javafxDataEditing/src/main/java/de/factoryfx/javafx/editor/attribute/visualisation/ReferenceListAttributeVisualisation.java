package de.factoryfx.javafx.editor.attribute.visualisation;

import java.util.List;
import java.util.function.Supplier;

import de.factoryfx.data.Data;
import de.factoryfx.javafx.editor.attribute.ListAttributeEditorVisualisation;
import de.factoryfx.javafx.editor.data.DataEditor;
import de.factoryfx.javafx.util.UniformDesign;
import de.factoryfx.javafx.widget.datalistedit.DataListEditWidget;
import de.factoryfx.javafx.widget.table.TableControlWidget;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class ReferenceListAttributeVisualisation extends ListAttributeEditorVisualisation<Data> {

    private final UniformDesign uniformDesign;
    private final DataEditor dataEditor;
    private final Runnable emptyAdder;
    private final Supplier<List<Data>> possibleValuesProvider;
    private final boolean isUserEditable;
    private final boolean isUserSelectable;

    public ReferenceListAttributeVisualisation(UniformDesign uniformDesign, DataEditor dataEditor, Runnable emptyAdder, Supplier<List<Data>> possibleValuesProvider, boolean isUserEditable, boolean isUserSelectable) {
        this.uniformDesign = uniformDesign;
        this.dataEditor = dataEditor;
        this.emptyAdder = emptyAdder;
        this.possibleValuesProvider = possibleValuesProvider;
        this.isUserEditable = isUserEditable;
        this.isUserSelectable = isUserSelectable;
    }


    @Override
    public Node createContent(ObservableList<Data> attributeValue) {
        TableView<Data> tableView = new TableView<>();
        tableView.setItems(attributeValue);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableColumn<Data, String> test = new TableColumn<>("Data");
        test.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().internal().getDisplayText()));
        tableView.getColumns().add(test);
        tableView.getStyleClass().add("hidden-tableview-headers");

        tableView.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                if (mouseEvent.getClickCount() == 2 && tableView.getSelectionModel().getSelectedItem()!=null) {
                    dataEditor.edit(tableView.getSelectionModel().getSelectedItem());
                }
            }
        });

        TableControlWidget<Data> tableControlWidget = new TableControlWidget<>(tableView,uniformDesign);
        Node tableControlWidgetContent = tableControlWidget.createContent();
        HBox.setHgrow(tableControlWidgetContent, Priority.ALWAYS);
        HBox.setMargin(tableControlWidgetContent, new Insets(0,1,0,0));

        final DataListEditWidget dataListEditWidget = new DataListEditWidget(attributeValue, tableView, dataEditor, uniformDesign, emptyAdder, possibleValuesProvider, isUserEditable, isUserSelectable);
        HBox buttons = (HBox)dataListEditWidget.createContent();
        buttons.getChildren().add(tableControlWidgetContent);

        VBox vbox = new VBox();
        VBox.setVgrow(tableView,Priority.ALWAYS);
        vbox.getChildren().add(tableView);
        vbox.getChildren().add(buttons);
        return vbox;
    }
}
