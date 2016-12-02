package de.factoryfx.javafx.editor.attribute.visualisation;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import de.factoryfx.data.Data;
import de.factoryfx.data.util.LanguageText;
import de.factoryfx.javafx.editor.attribute.ListAttributeEditorVisualisation;
import de.factoryfx.javafx.editor.data.DataEditor;
import de.factoryfx.javafx.util.DataChoiceDialog;
import de.factoryfx.javafx.util.UniformDesign;
import de.factoryfx.javafx.widget.table.TableControlWidget;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.controlsfx.glyphfont.FontAwesome;

public class ReferenceListAttributeVisualisation extends ListAttributeEditorVisualisation<Data> {

    private LanguageText selectText= new LanguageText().en("select").de("Auswählen");
    private LanguageText addText= new LanguageText().en("add").de("Hinzufügen");
    private LanguageText deleteText= new LanguageText().en("delete").de("Löschen");
    private LanguageText editText= new LanguageText().en("edit").de("Editieren");
    private LanguageText copyText= new LanguageText().en("copy").de("Kopieren");

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


        Button showButton = new Button("", uniformDesign.createIcon(FontAwesome.Glyph.PENCIL));
        showButton.setOnAction(event -> dataEditor.edit(tableView.getSelectionModel().getSelectedItem()));
        showButton.disableProperty().bind(tableView.getSelectionModel().selectedItemProperty().isNull());

        Button selectButton = new Button("", uniformDesign.createIcon(FontAwesome.Glyph.SEARCH_PLUS));
        selectButton.setOnAction(event -> {
            Optional<Data> toAdd = new DataChoiceDialog().show(possibleValuesProvider.get(),selectButton.getScene().getWindow(),uniformDesign);
            toAdd.ifPresent(data -> attributeValue.add(data));
        });
        selectButton.setDisable(!isUserEditable || !isUserSelectable);

        Button adderButton = new Button();
        uniformDesign.addIcon(adderButton,FontAwesome.Glyph.PLUS);
        adderButton.setOnAction(event -> {
            emptyAdder.run();
            dataEditor.edit(attributeValue.get(attributeValue.size()-1));
        });
        adderButton.setDisable(!isUserEditable);

        Button deleteButton = new Button();
        uniformDesign.addDangerIcon(deleteButton,FontAwesome.Glyph.TIMES);
        deleteButton.setOnAction(event -> attributeValue.remove(tableView.getSelectionModel().getSelectedItem()));
        deleteButton.disableProperty().bind(tableView.getSelectionModel().selectedItemProperty().isNull().or(new SimpleBooleanProperty(!isUserEditable)));

        Button moveUpButton = new Button();
        uniformDesign.addIcon(moveUpButton,FontAwesome.Glyph.ANGLE_UP);
        moveUpButton.disableProperty().bind(tableView.getSelectionModel().selectedItemProperty().isNull());
        moveUpButton.setOnAction(event -> {
            int selectedIndex = tableView.getSelectionModel().getSelectedIndex();
            if (selectedIndex -1>=0){
                Collections.swap(attributeValue, selectedIndex, selectedIndex -1);
                tableView.getSelectionModel().select(selectedIndex -1);
            }
        });
        Button moveDownButton = new Button();
        uniformDesign.addIcon(moveDownButton,FontAwesome.Glyph.ANGLE_DOWN);
        moveDownButton.disableProperty().bind(tableView.getSelectionModel().selectedItemProperty().isNull());
        moveDownButton.setOnAction(event -> {
            int selectedIndex = tableView.getSelectionModel().getSelectedIndex();
            if (selectedIndex+1<tableView.getItems().size()){
                Collections.swap(attributeValue, selectedIndex, selectedIndex +1);
                tableView.getSelectionModel().select(selectedIndex +1);
            }
        });

        Button copyButton = new Button();
        uniformDesign.addIcon(copyButton,FontAwesome.Glyph.COPY);
        copyButton.disableProperty().bind(tableView.getSelectionModel().selectedItemProperty().isNull());
        copyButton.setOnAction(event -> {
            attributeValue.add(tableView.getSelectionModel().getSelectedItem().utility().semanticCopy());
        });

        HBox buttons = new HBox();
        buttons.setAlignment(Pos.CENTER_LEFT);
        buttons.setSpacing(3);
        buttons.getChildren().add(showButton);
        buttons.getChildren().add(selectButton);
        buttons.getChildren().add(adderButton);
        buttons.getChildren().add(copyButton);
        buttons.getChildren().add(deleteButton);
        buttons.getChildren().add(moveUpButton);
        buttons.getChildren().add(moveDownButton);

        showButton.setTooltip(new Tooltip(uniformDesign.getText(editText)));
        selectButton.setTooltip(new Tooltip(uniformDesign.getText(selectText)));
        adderButton.setTooltip(new Tooltip(uniformDesign.getText(addText)));
        deleteButton.setTooltip(new Tooltip(uniformDesign.getText(deleteText)));
        copyButton.setTooltip(new Tooltip(uniformDesign.getText(copyText)));

        HBox.setMargin(moveUpButton,new Insets(0,0,0,9));
        HBox.setMargin(moveDownButton,new Insets(0,9,0,0));

        TableControlWidget<Data> tableControlWidget = new TableControlWidget<>(tableView,uniformDesign);
        Node tableControlWidgetContent = tableControlWidget.createContent();
        HBox.setHgrow(tableControlWidgetContent, Priority.ALWAYS);
        HBox.setMargin(tableControlWidgetContent, new Insets(0,1,0,0));
        buttons.getChildren().add(tableControlWidgetContent);

        VBox vbox = new VBox();
        VBox.setVgrow(tableView,Priority.ALWAYS);
        vbox.getChildren().add(tableView);
        vbox.getChildren().add(buttons);
        return vbox;
    }
}
