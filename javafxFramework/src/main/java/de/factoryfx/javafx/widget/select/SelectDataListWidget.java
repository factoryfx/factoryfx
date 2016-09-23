package de.factoryfx.javafx.widget.select;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import de.factoryfx.javafx.util.UniformDesign;
import de.factoryfx.javafx.widget.TableInitializer;
import de.factoryfx.javafx.widget.Widget;
import de.factoryfx.javafx.widget.table.TableControlWidget;
import javafx.beans.InvalidationListener;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.controlsfx.glyphfont.FontAwesome;

public class SelectDataListWidget<T> implements Widget {
    public final SimpleObjectProperty<T> selected = new SimpleObjectProperty<>();
    private final TableInitializer<T> tableInitializer;
    private final Supplier<ObservableList<T>> listProvider;
    private final Optional<Supplier<T>> emptyAdder;//add the item and return the already added item
    private final Optional<Consumer<T>> itemDeleter;
    SimpleObjectProperty<EventHandler<MouseEvent>> mouseClickedHandler = new SimpleObjectProperty<>();
    boolean setFlag = false;
    private final UniformDesign uniformDesign;

    public SelectDataListWidget(TableInitializer<T> tableInitializer, Supplier<ObservableList<T>> listProvider, Supplier<T> emptyAdder, Consumer<T> itemDeleter, UniformDesign uniformDesign) {
        this.tableInitializer = tableInitializer;
        this.listProvider = listProvider;
        this.emptyAdder = Optional.ofNullable(emptyAdder);
        this.itemDeleter = Optional.ofNullable(itemDeleter);
        this.uniformDesign = uniformDesign;
    }

    @Override
    public Node createContent() {
        TableView<T> tableView = new TableView<>();
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.getSelectionModel().selectedItemProperty();

        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            setFlag = true;
            selected.set(newValue);
            setFlag = false;
        });
        selected.addListener((observable, oldValue, newValue) -> {
            if (!setFlag) {
                tableView.getSelectionModel().clearSelection();
                tableView.getSelectionModel().select(newValue);
                tableView.scrollTo(tableView.getSelectionModel().getSelectedItem());
            }
        });

        tableInitializer.initTable(tableView);
        tableView.setItems(listProvider.get());

        VBox vBox = new VBox();
        VBox.setVgrow(tableView, Priority.ALWAYS);
        vBox.getChildren().add(tableView);
        TableControlWidget<T> tableControlWidget = new TableControlWidget<T>(tableView, uniformDesign);
        Node tableControlWidgetContent = tableControlWidget.createContent();
        VBox.setMargin(tableControlWidgetContent, new Insets(3, 0, 3, 0));

        HBox hBox = new HBox(3);
        hBox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(tableControlWidgetContent, Priority.ALWAYS);
        HBox.setMargin(tableControlWidgetContent, new Insets(1));
        hBox.getChildren().add(tableControlWidgetContent);
        if (emptyAdder.isPresent() || itemDeleter.isPresent()) {
            hBox.getChildren().add(new Separator(Orientation.VERTICAL));
        }

        if (emptyAdder.isPresent()) {
            Button newButton = new Button();
            uniformDesign.addIcon(newButton,FontAwesome.Glyph.PLUS);
            newButton.setOnAction(event -> {
                tableControlWidget.clearFilter();
                T newItem = emptyAdder.get().get();

                select(newItem);
            });
            hBox.getChildren().add(newButton);
        }

        if (itemDeleter.isPresent()) {
            Button deleteButton = new Button();
            uniformDesign.addDangerIcon(deleteButton,FontAwesome.Glyph.TIMES);
            deleteButton.setOnAction(event -> itemDeleter.get().accept(tableView.getSelectionModel().getSelectedItem()));
            deleteButton.disableProperty().bind(tableView.getSelectionModel().selectedItemProperty().isNull());
            hBox.getChildren().add(deleteButton);
        }

        InvalidationListener listener = observable -> {
            tableView.setOnMouseClicked(mouseClickedHandler.get());
        };
        mouseClickedHandler.addListener(listener);
        listener.invalidated(mouseClickedHandler);

        vBox.getChildren().add(hBox);
        return vBox;
    }

    public void select(T item) {
        selected.set(item);
    }

    public void setOnMouseClicked(EventHandler<MouseEvent> eventEventHandler) {
        mouseClickedHandler.set(eventEventHandler);
    }

}
