package de.factoryfx.javafx.data.editor.attribute.visualisation;

import de.factoryfx.data.attribute.ValueListAttribute;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import org.controlsfx.glyphfont.FontAwesome;

import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.data.attribute.AttributeChangeListener;
import de.factoryfx.javafx.data.editor.attribute.AttributeEditor;
import de.factoryfx.javafx.data.editor.attribute.ListAttributeEditorVisualisation;
import de.factoryfx.javafx.data.util.TypedTextFieldHelper;
import de.factoryfx.javafx.data.util.UniformDesign;
import de.factoryfx.javafx.data.widget.table.TableControlWidget;

public class ValueListAttributeVisualisation<T> extends ListAttributeEditorVisualisation<T> {
    private final UniformDesign uniformDesign;
    private final Attribute<T,?> detailAttribute;
    private final AttributeEditor<T,?> attributeEditor;
    private final ValueListAttribute valueListAttribute;

    public ValueListAttributeVisualisation(UniformDesign uniformDesign, Attribute<T,?> detailAttribute, AttributeEditor<T,?> attributeEditor, ValueListAttribute valueListAttribute) {
        this.uniformDesign = uniformDesign;
        this.detailAttribute = detailAttribute;
        this.attributeEditor = attributeEditor;
        this.valueListAttribute = valueListAttribute;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Node createContent(ObservableList<T> readOnlyList, boolean readonly) {
        TextField textField = new TextField();
        TypedTextFieldHelper.setupLongTextField(textField);
//        textField.textProperty().bindBidirectional(boundTo, new LongStringConverter());

        TableView<T> tableView = new TableView<>();
        tableView.setItems(readOnlyList);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableColumn<T, String> test = new TableColumn<>("test");
        test.setCellValueFactory(param -> new SimpleStringProperty(""+param.getValue()));
        tableView.getColumns().add(test);
        tableView.getStyleClass().add("hidden-tableview-headers");

//        tableInitializer.initTable(tableView);
//        tableView.setItems(tableItems);


        Button addButton=new Button("", uniformDesign.createIcon(FontAwesome.Glyph.PLUS));
        addButton.setOnAction(event -> {
            valueListAttribute.add(detailAttribute.get());
        });

        Button replaceButton=new Button("", uniformDesign.createIcon(FontAwesome.Glyph.EXCHANGE));
        replaceButton.setOnAction(event -> {
            int selectedIndex = tableView.getSelectionModel().getSelectedIndex();
            valueListAttribute.set(selectedIndex, detailAttribute.get());
        });

        Button deleteButton = new Button("");
        uniformDesign.addDangerIcon(deleteButton,FontAwesome.Glyph.TIMES);
        deleteButton.setOnAction(event -> {
            valueListAttribute.remove(tableView.getSelectionModel().getSelectedItem());
        });
        deleteButton.disableProperty().bind(tableView.getSelectionModel().selectedItemProperty().isNull());

        tableView.getSelectionModel().selectedItemProperty().addListener(observable -> {
            detailAttribute.set(tableView.getSelectionModel().getSelectedItem());
        });
        AttributeChangeListener detailAttributeChangeListener = (attribute1, value) -> {
            Platform.runLater(() -> {
                if (value instanceof String){
                    addButton.setDisable("".equals(value));
                } else {
                    addButton.setDisable(value == null);
                }
                if (!addButton.isDisabled()){//bug workaround disable state styling doesn't work
                    addButton.setOpacity(1);
                } else {
                    addButton.setOpacity(0.4);
                }
                replaceButton.setDisable(value == null || tableView.getSelectionModel().getSelectedItem()==null);
                if (!replaceButton.isDisabled()){//bug workaround disable state styling doesn't work
                    replaceButton.setOpacity(1);
                } else {
                    replaceButton.setOpacity(0.4);
                }
            });
        };
        detailAttribute.internal_addListener(detailAttributeChangeListener);
        detailAttributeChangeListener.changed(detailAttribute,detailAttribute.get());

        VBox vBox  = new VBox();
        VBox.setVgrow(tableView, Priority.ALWAYS);
        vBox.getChildren().add(tableView);
        tableView.setMinHeight(100);
        HBox listControls = new HBox();
        listControls.setAlignment(Pos.CENTER_LEFT);
        VBox.setMargin(listControls, new Insets(0, 0, 0, 0));
        listControls.setSpacing(3);
//        listControls.getChildren().add(addButton);
//        listControls.getChildren().add(replaceButton);
        listControls.getChildren().add(deleteButton);
        vBox.getChildren().add(listControls);

        TableControlWidget<?> tableControlWidget = new TableControlWidget<>(tableView,uniformDesign);
        Node tableControlWidgetContent = tableControlWidget.createContent();
        HBox.setHgrow(tableControlWidgetContent,Priority.ALWAYS);
        HBox.setMargin(tableControlWidgetContent, new Insets(0,1,0,0));
        listControls.getChildren().add(tableControlWidgetContent);

        HBox editorWrapper= new HBox(3);
        editorWrapper.setAlignment(Pos.CENTER_LEFT);
        editorWrapper.setPadding(new Insets(3));
        editorWrapper.getChildren().add(new Label(uniformDesign.getLabelText(detailAttribute)));
        Node content = attributeEditor.createContent();

        HBox.setHgrow(content,Priority.ALWAYS);
        editorWrapper.getChildren().addAll(content,addButton,replaceButton);
        vBox.getChildren().add(new Separator());
        vBox.getChildren().add(editorWrapper);

        listControls.setDisable(readonly);

//        editorWrapper.disableProperty().edit(tableView.getSelectionModel().selectedItemProperty().isNull().and(content.focusedProperty().not()));
        return vBox;
    }

}
