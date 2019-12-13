package io.github.factoryfx.javafx.editor.attribute.visualisation;

import io.github.factoryfx.javafx.editor.attribute.AttributeVisualisation;
import io.github.factoryfx.javafx.editor.attribute.ListAttributeVisualisation;
import io.github.factoryfx.javafx.editor.attribute.ValidationDecoration;
import io.github.factoryfx.javafx.widget.table.TableControlWidget;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
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

import io.github.factoryfx.factory.attribute.Attribute;
import io.github.factoryfx.factory.attribute.AttributeChangeListener;
import io.github.factoryfx.javafx.util.TypedTextFieldHelper;
import io.github.factoryfx.javafx.util.UniformDesign;

import java.util.List;

public class ValueListAttributeVisualisation<T, A extends Attribute<List<T>,A>, AD extends Attribute<T,AD>> extends ListAttributeVisualisation<T,A> {
    private final UniformDesign uniformDesign;
    private final AD detailAttribute;
    private final AttributeVisualisation detailAttributeVisualisation;
    private final A valueListAttribute;

    public ValueListAttributeVisualisation(A valueListAttribute, ValidationDecoration validationDecoration, UniformDesign uniformDesign, AD detailAttribute, AttributeVisualisation detailAttributeVisualisation) {
        super(valueListAttribute,validationDecoration);
        this.uniformDesign = uniformDesign;
        this.detailAttribute = detailAttribute;
        this.detailAttributeVisualisation = detailAttributeVisualisation;
        this.valueListAttribute = valueListAttribute;
    }

    @Override
    public Node createValueListVisualisation() {
        TextField textField = new TextField();
        TypedTextFieldHelper.setupLongTextField(textField);
//        textField.textProperty().bindBidirectional(boundTo, new LongStringConverter());

        TableView<T> tableView = new TableView<>();
        tableView.setItems(readOnlyObservableList);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableColumn<T, String> test = new TableColumn<>("test");
        test.setCellValueFactory(param -> new SimpleStringProperty(""+param.getValue()));
        tableView.getColumns().add(test);
        tableView.getStyleClass().add("hidden-tableview-headers");

//        tableInitializer.initTable(tableView);
//        tableView.setItems(tableItems);


        Button addButton=new Button("", uniformDesign.createIcon(FontAwesome.Glyph.PLUS));
        addButton.setOnAction(event -> {
            T newValue = detailAttribute.get();
            valueListAttribute.get().add(newValue);
            tableView.getSelectionModel().clearSelection();
            tableView.getSelectionModel().select(newValue);
        });

        Button replaceButton=new Button("", uniformDesign.createIcon(FontAwesome.Glyph.EXCHANGE));
        replaceButton.setOnAction(event -> {
            int selectedIndex = tableView.getSelectionModel().getSelectedIndex();
            valueListAttribute.get().set(selectedIndex, detailAttribute.get());
        });

        Button deleteButton = new Button("");
        uniformDesign.addDangerIcon(deleteButton,FontAwesome.Glyph.TIMES);
        deleteButton.setOnAction(event -> {
            valueListAttribute.get().remove(tableView.getSelectionModel().getSelectedItem());
        });
        deleteButton.disableProperty().bind(tableView.getSelectionModel().selectedItemProperty().isNull());

        tableView.getSelectionModel().selectedItemProperty().addListener(observable -> {
            detailAttribute.set(tableView.getSelectionModel().getSelectedItem());
        });
        AttributeChangeListener<T,AD> detailAttributeChangeListener = (attribute1, value) -> {
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
        Node content = detailAttributeVisualisation.createVisualisation();

        HBox.setHgrow(content,Priority.ALWAYS);
        editorWrapper.getChildren().addAll(content,addButton,replaceButton);
        vBox.getChildren().add(new Separator());
        vBox.getChildren().add(editorWrapper);

//        editorWrapper.disableProperty().edit(tableView.getSelectionModel().selectedItemProperty().isNull().and(content.focusedProperty().not()));
        listControls.disableProperty().bind(readOnly);
        return vBox;
    }

}
