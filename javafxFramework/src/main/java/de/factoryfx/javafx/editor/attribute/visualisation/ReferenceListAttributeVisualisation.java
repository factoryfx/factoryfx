package de.factoryfx.javafx.editor.attribute.visualisation;

import java.util.List;
import java.util.function.Supplier;

import de.factoryfx.data.Data;
import de.factoryfx.javafx.editor.attribute.AttributeEditorVisualisation;
import de.factoryfx.javafx.editor.data.DataEditor;
import de.factoryfx.javafx.util.UniformDesign;
import de.factoryfx.javafx.widget.table.TableControlWidget;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.controlsfx.glyphfont.FontAwesome;

public class ReferenceListAttributeVisualisation implements AttributeEditorVisualisation<ObservableList<Data>> {

    private final UniformDesign uniformDesign;
    private final DataEditor dataEditor;
    private final Runnable emptyAdder;
    private final Supplier<List<? extends Data>> possibleValuesProvider;

    public ReferenceListAttributeVisualisation(UniformDesign uniformDesign, DataEditor dataEditor, Runnable emptyAdder, Supplier<List<? extends Data>> possibleValuesProvider) {
        this.uniformDesign = uniformDesign;
        this.dataEditor = dataEditor;
        this.emptyAdder = emptyAdder;
        this.possibleValuesProvider = possibleValuesProvider;
    }

    @Override
    public Node createContent(SimpleObjectProperty<ObservableList<Data>> boundTo) {
        TableView<Data> tableView = new TableView<>();
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableColumn<Data, String> test = new TableColumn<>("Data");
        test.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getDisplayText()));
        tableView.getColumns().add(test);
        tableView.getStyleClass().add("hidden-tableview-headers");

        tableView.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                if (mouseEvent.getClickCount() == 2 && tableView.getSelectionModel().getSelectedItem()!=null) {
                    dataEditor.edit(tableView.getSelectionModel().getSelectedItem());
                }
            }
        });

        //invalidation listener for boundTo don't work
        ListChangeListener<Data> dataListChangeListener = c -> {
            if (boundTo.get() == null) {
                tableView.setItems(null);
            } else {
                tableView.setItems(boundTo.get());
            }
        };
        boundTo.get().addListener(dataListChangeListener);
        dataListChangeListener.onChanged(null);



        Button showButton = new Button("", uniformDesign.createIcon(FontAwesome.Glyph.PENCIL));
        showButton.setOnAction(event -> dataEditor.edit(tableView.getSelectionModel().getSelectedItem()));
        showButton.disableProperty().bind(tableView.getSelectionModel().selectedItemProperty().isNull());


        Button selectButton = new Button("", uniformDesign.createIcon(FontAwesome.Glyph.SEARCH_PLUS));
        selectButton.setOnAction(event -> {
//            boundTo.get().add(new DataChoiceDialog().show(possibleValuesProvider.get()));
        });

        Button adderButton = new Button();
        uniformDesign.addIcon(adderButton,FontAwesome.Glyph.PLUS);
        adderButton.setOnAction(event -> emptyAdder.run());

        Button deleteButton = new Button();
        uniformDesign.addDangerIcon(deleteButton,FontAwesome.Glyph.TIMES);
        deleteButton.setOnAction(event -> boundTo.get().remove(tableView.getSelectionModel().getSelectedItem()));
        deleteButton.disableProperty().bind(tableView.getSelectionModel().selectedItemProperty().isNull());


        HBox buttons = new HBox();
        buttons.setAlignment(Pos.CENTER_LEFT);
        buttons.setSpacing(3);
        buttons.getChildren().add(showButton);
        buttons.getChildren().add(selectButton);
        buttons.getChildren().add(adderButton);
        buttons.getChildren().add(deleteButton);

        TableControlWidget<Data> tableControlWidget = new TableControlWidget<>(tableView,uniformDesign);
        Node tableControlWidgetContent = tableControlWidget.createContent();
        HBox.setHgrow(tableControlWidgetContent, Priority.ALWAYS);
        HBox.setMargin(tableControlWidgetContent, new Insets(0,1,0,0));
        buttons.getChildren().add(tableControlWidgetContent);

        VBox vbox = new VBox();
        vbox.getChildren().add(tableView);
        vbox.getChildren().add(buttons);

        return vbox;
    }
}