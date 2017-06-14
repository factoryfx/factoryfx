package de.factoryfx.javafx.editor.attribute.visualisation;

import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.data.attribute.AttributeChangeListener;
import de.factoryfx.javafx.editor.attribute.AttributeEditor;
import de.factoryfx.javafx.editor.attribute.ListAttributeEditorVisualisation;
import de.factoryfx.javafx.util.TypedTextFieldHelper;
import de.factoryfx.javafx.util.UniformDesign;
import de.factoryfx.javafx.widget.table.TableControlWidget;
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

import java.util.List;

public class ValueListAttributeVisualisation<T> extends ListAttributeEditorVisualisation<T> {
    private final UniformDesign uniformDesign;
    private final Attribute<T,?> detailAttribute;
    private final AttributeEditor<T,?> attributeEditor;

    public ValueListAttributeVisualisation(UniformDesign uniformDesign, Attribute<T,?> detailAttribute, AttributeEditor<T,?> attributeEditor) {
        this.uniformDesign = uniformDesign;
        this.detailAttribute = detailAttribute;
        this.attributeEditor = attributeEditor;
    }

    @Override
    public Node createContent(List<T> attributeValue) {
        TextField textField = new TextField();
        TypedTextFieldHelper.setupLongTextField(textField);
//        textField.textProperty().bindBidirectional(boundTo, new LongStringConverter());

        TableView<T> tableView = new TableView<>();
        tableView.setItems((ObservableList<T>)attributeValue);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableColumn<T, String> test = new TableColumn<>("test");
        test.setCellValueFactory(param -> new SimpleStringProperty(""+param.getValue()));
        tableView.getColumns().add(test);
        tableView.getStyleClass().add("hidden-tableview-headers");

//        tableInitializer.initTable(tableView);
//        tableView.setItems(tableItems);


        Button addButton=new Button("", uniformDesign.createIcon(FontAwesome.Glyph.PLUS));
        addButton.setOnAction(event -> {
            attributeValue.add(detailAttribute.get());
        });

        Button replaceButton=new Button("", uniformDesign.createIcon(FontAwesome.Glyph.EXCHANGE));
        replaceButton.setOnAction(event -> {
            int selectedIndex = tableView.getSelectionModel().getSelectedIndex();
            attributeValue.set(selectedIndex, detailAttribute.get());
        });

        Button deleteButton = new Button("");
        uniformDesign.addDangerIcon(deleteButton,FontAwesome.Glyph.TIMES);
        deleteButton.setOnAction(event -> attributeValue.remove(tableView.getSelectionModel().getSelectedItem()));
        deleteButton.disableProperty().bind(tableView.getSelectionModel().selectedItemProperty().isNull());

        tableView.getSelectionModel().selectedItemProperty().addListener(observable -> {
            detailAttribute.set(tableView.getSelectionModel().getSelectedItem());
        });
        AttributeChangeListener detailAttributeChangeListener = (attribute1, value) -> {
            Platform.runLater(() -> {
                addButton.setDisable(value == null);
                replaceButton.setDisable(value == null || tableView.getSelectionModel().getSelectedItem()==null);
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

//        editorWrapper.disableProperty().edit(tableView.getSelectionModel().selectedItemProperty().isNull().and(content.focusedProperty().not()));
        return vBox;
    }

}
