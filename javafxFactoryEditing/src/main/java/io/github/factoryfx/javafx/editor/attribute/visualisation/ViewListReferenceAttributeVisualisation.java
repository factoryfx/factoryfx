package io.github.factoryfx.javafx.editor.attribute.visualisation;

import java.util.function.Consumer;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryViewListAttribute;
import io.github.factoryfx.javafx.editor.attribute.ValidationDecoration;
import io.github.factoryfx.javafx.editor.attribute.ListAttributeVisualisation;
import io.github.factoryfx.javafx.widget.table.TableControlWidget;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
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

import io.github.factoryfx.javafx.util.UniformDesign;

public class ViewListReferenceAttributeVisualisation<R extends FactoryBase<?,R>,L, F extends FactoryBase<L,R>, A extends FactoryViewListAttribute<R,L, F>> extends ListAttributeVisualisation<F, FactoryViewListAttribute<R,L, F>> {

    private final Consumer<FactoryBase<?,?>> navigateToData;
    private final UniformDesign uniformDesign;
    private final A attribute;

    public ViewListReferenceAttributeVisualisation(A attribute, ValidationDecoration validationDecoration,Consumer<FactoryBase<?,?>> navigateToData, UniformDesign uniformDesign) {
        super(attribute, validationDecoration);
        this.navigateToData = navigateToData;
        this.uniformDesign = uniformDesign;
        this.attribute=attribute;
        this.attribute.setRunlaterExecutor(Platform::runLater);
    }

    @Override
    public Node createValueListVisualisation() {
        TableView<FactoryBase<?,?>> tableView = new TableView<>();
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableColumn<FactoryBase<?,?>, String> test = new TableColumn<>("Data");
        test.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().internal().getDisplayText()));
        tableView.getColumns().add(test);
        tableView.getStyleClass().add("hidden-tableview-headers");
        ObservableList<FactoryBase<?,?>> items = FXCollections.observableArrayList();
        tableView.setItems(items);

        tableView.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                if (mouseEvent.getClickCount() == 2 && tableView.getSelectionModel().getSelectedItem()!=null) {
                    navigateToData.accept(tableView.getSelectionModel().getSelectedItem());
                }
            }
        });

        this.readOnlyObservableList.addListener((InvalidationListener) observable -> items.setAll(readOnlyObservableList));
        items.setAll(readOnlyObservableList);


        TableControlWidget<FactoryBase<?,?>> tableControlWidget = new TableControlWidget<>(tableView,uniformDesign);
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
