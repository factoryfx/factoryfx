package io.github.factoryfx.javafx.factory.editor.attribute.visualisation;

import java.util.function.Consumer;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryListBaseAttribute;
import io.github.factoryfx.javafx.factory.editor.attribute.ValidationDecoration;
import io.github.factoryfx.javafx.factory.editor.attribute.ListAttributeVisualisation;
import io.github.factoryfx.javafx.factory.widget.datalistedit.ReferenceListAttributeEditWidget;
import io.github.factoryfx.javafx.factory.widget.table.TableControlWidget;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import io.github.factoryfx.javafx.factory.util.UniformDesign;

public class ReferenceListAttributeVisualisation<T extends FactoryBase<?,?>, A extends FactoryListBaseAttribute<?,?,T,A>> extends ListAttributeVisualisation<T,A> {

    private final UniformDesign uniformDesign;
    private final Consumer<FactoryBase<?,?>> navigateToData;
    private final ReferenceListAttributeEditWidget<?,?,FactoryBase<?,?>> dataListEditWidget;
    private final TableView<T> tableView;
    private DoubleBinding heightBinding;

    public ReferenceListAttributeVisualisation(A attribute, ValidationDecoration validationDecoration, UniformDesign uniformDesign, Consumer<FactoryBase<?,?>> navigateToData, TableView<T> tableView, ReferenceListAttributeEditWidget<?,?,FactoryBase<?,?>> dataListEditWidget) {
        super(attribute,validationDecoration);
        this.uniformDesign = uniformDesign;
        this.navigateToData = navigateToData;
        this.dataListEditWidget = dataListEditWidget;
        this.tableView = tableView;
    }


    @Override
    public Node createValueListVisualisation() {
        tableView.setItems(readOnlyObservableList);

        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableColumn<T, String> test = new TableColumn<>("Data");
        test.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().internal().getDisplayText()));
        tableView.getColumns().add(test);
        tableView.getStyleClass().add("hidden-tableview-headers");
        heightBinding = Bindings.createDoubleBinding(() ->
                readOnlyObservableList.size() < 4 ? 74d : 243d, readOnlyObservableList);
        tableView.prefHeightProperty().bind(heightBinding);

        tableView.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                if (mouseEvent.getClickCount() == 2 && tableView.getSelectionModel().getSelectedItem()!=null) {
                    navigateToData.accept(tableView.getSelectionModel().getSelectedItem());
                }
            }
        });

        TableControlWidget<T> tableControlWidget = new TableControlWidget<>(tableView,uniformDesign);
        Node tableControlWidgetContent = tableControlWidget.createContent();
        HBox.setHgrow(tableControlWidgetContent, Priority.ALWAYS);
        HBox.setMargin(tableControlWidgetContent, new Insets(0,1,0,0));

        HBox buttons = dataListEditWidget.createContent();
        buttons.getChildren().add(tableControlWidgetContent);
        buttons.disableProperty().bind(readOnly);


        VBox vbox = new VBox();
        VBox.setVgrow(tableView,Priority.ALWAYS);
        vbox.getChildren().add(tableView);
        vbox.getChildren().add(buttons);
        return vbox;
    }



}
