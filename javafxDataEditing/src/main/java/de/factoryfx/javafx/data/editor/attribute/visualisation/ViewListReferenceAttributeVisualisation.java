package de.factoryfx.javafx.data.editor.attribute.visualisation;

import java.util.List;
import java.util.function.Consumer;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.javafx.data.editor.attribute.AttributeEditorVisualisation;
import de.factoryfx.javafx.data.util.UniformDesign;
import de.factoryfx.javafx.data.widget.table.TableControlWidget;

public class ViewListReferenceAttributeVisualisation implements AttributeEditorVisualisation<List<Data>> {

    private final Consumer<Data> navigateToData;
    private final UniformDesign uniformDesign;

    public ViewListReferenceAttributeVisualisation(Consumer<Data> navigateToData, UniformDesign uniformDesign) {
        this.navigateToData = navigateToData;
        this.uniformDesign = uniformDesign;
    }




    @Override
    public void init(Attribute<List<Data>,?> boundAttribute) {
        updater.accept(boundAttribute.get());
    }

    @Override
    public void attributeValueChanged(List<Data> newValue) {
        updater.accept(newValue);
    }

    private Consumer<List<Data>> updater = data -> {};

    @Override
    public Node createVisualisation() {
        TableView<Data> tableView = new TableView<>();
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableColumn<Data, String> test = new TableColumn<>("Data");
        test.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().internal().getDisplayText()));
        tableView.getColumns().add(test);
        tableView.getStyleClass().add("hidden-tableview-headers");
        ObservableList<Data> items = FXCollections.observableArrayList();
        tableView.setItems(items);

        tableView.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                if (mouseEvent.getClickCount() == 2 && tableView.getSelectionModel().getSelectedItem()!=null) {
                    navigateToData.accept(tableView.getSelectionModel().getSelectedItem());
                }
            }
        });

        updater=datas -> {
            if (datas == null) {
                items.clear();
            } else {
                items.setAll(datas);
            }
        };

        TableControlWidget<Data> tableControlWidget = new TableControlWidget<>(tableView,uniformDesign);
        Node tableControlWidgetContent = tableControlWidget.createContent();
        HBox.setHgrow(tableControlWidgetContent, Priority.ALWAYS);
        HBox.setMargin(tableControlWidgetContent, new Insets(0,1,0,0));

        VBox vbox = new VBox();
        VBox.setVgrow(tableView,Priority.ALWAYS);
        vbox.getChildren().add(tableView);
        vbox.getChildren().add(tableControlWidgetContent);
        return vbox;
    }
}
