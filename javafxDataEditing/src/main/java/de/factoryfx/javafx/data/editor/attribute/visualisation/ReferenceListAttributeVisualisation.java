package de.factoryfx.javafx.data.editor.attribute.visualisation;

import java.util.function.Consumer;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
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

import de.factoryfx.data.Data;
import de.factoryfx.javafx.data.editor.attribute.ListAttributeEditorVisualisation;
import de.factoryfx.javafx.data.util.UniformDesign;
import de.factoryfx.javafx.data.widget.datalistedit.ReferenceListAttributeEditWidget;
import de.factoryfx.javafx.data.widget.table.TableControlWidget;

public class ReferenceListAttributeVisualisation extends ListAttributeEditorVisualisation<Data> {

    private final UniformDesign uniformDesign;
    private final Consumer<Data> navigateToData;
    private final ReferenceListAttributeEditWidget<Data> dataListEditWidget;
    private final TableView<Data> tableView;
    private DoubleBinding heightBinding;

    public ReferenceListAttributeVisualisation(UniformDesign uniformDesign, Consumer<Data> navigateToData, TableView<Data> tableView, ReferenceListAttributeEditWidget<Data> dataListEditWidget) {
        this.uniformDesign = uniformDesign;
        this.navigateToData = navigateToData;
        this.dataListEditWidget = dataListEditWidget;
        this.tableView = tableView;
    }


    @Override
    public Node createContent(ObservableList<Data> readOnlyList, boolean readonly) {
        tableView.setItems(readOnlyList);

        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableColumn<Data, String> test = new TableColumn<>("Data");
        test.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().internal().getDisplayText()));
        tableView.getColumns().add(test);
        tableView.getStyleClass().add("hidden-tableview-headers");
        heightBinding = Bindings.createDoubleBinding(() ->
                readOnlyList.size() < 4 ? 74d : 243d, readOnlyList);
        tableView.prefHeightProperty().bind(heightBinding);

        tableView.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                if (mouseEvent.getClickCount() == 2 && tableView.getSelectionModel().getSelectedItem()!=null) {
                    navigateToData.accept(tableView.getSelectionModel().getSelectedItem());
                }
            }
        });

        TableControlWidget<Data> tableControlWidget = new TableControlWidget<>(tableView,uniformDesign);
        Node tableControlWidgetContent = tableControlWidget.createContent();
        HBox.setHgrow(tableControlWidgetContent, Priority.ALWAYS);
        HBox.setMargin(tableControlWidgetContent, new Insets(0,1,0,0));

        HBox buttons = (HBox)dataListEditWidget.createContent();
        buttons.getChildren().add(tableControlWidgetContent);

        VBox vbox = new VBox();
        VBox.setVgrow(tableView,Priority.ALWAYS);
        vbox.getChildren().add(tableView);
        vbox.getChildren().add(buttons);

        buttons.setDisable(readonly);
        return vbox;
    }
}
