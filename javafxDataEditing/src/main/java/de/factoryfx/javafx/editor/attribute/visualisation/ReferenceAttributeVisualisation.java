package de.factoryfx.javafx.editor.attribute.visualisation;

import java.util.List;
import java.util.function.Supplier;

import de.factoryfx.data.Data;
import de.factoryfx.javafx.editor.attribute.AttributeEditorVisualisation;
import de.factoryfx.javafx.editor.data.DataEditor;
import de.factoryfx.javafx.util.DataChoiceDialog;
import de.factoryfx.javafx.util.UniformDesign;
import javafx.beans.InvalidationListener;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import org.controlsfx.glyphfont.FontAwesome;

public class ReferenceAttributeVisualisation implements AttributeEditorVisualisation<Data> {

    private final UniformDesign uniformDesign;
    private final DataEditor dataEditor;
    private final Supplier<Data> emptyAdder;
    private final Supplier<List<Data>> possibleValuesProvider;

    public ReferenceAttributeVisualisation(UniformDesign uniformDesign, DataEditor dataEditor, Supplier<Data> emptyAdder, Supplier<List<Data>> possibleValuesProvider) {
        this.uniformDesign = uniformDesign;
        this.dataEditor = dataEditor;
        this.emptyAdder = emptyAdder;
        this.possibleValuesProvider = possibleValuesProvider;
    }

    @Override
    public Node createContent(SimpleObjectProperty<Data> boundTo) {
        Button showButton = new Button("", uniformDesign.createIcon(FontAwesome.Glyph.PENCIL));
        showButton.setOnAction(event -> dataEditor.edit(boundTo.get()));
        showButton.disableProperty().bind(boundTo.isNull());


        Button selectButton = new Button();
        uniformDesign.addIcon(selectButton,FontAwesome.Glyph.SEARCH_PLUS);
        selectButton.setOnAction(event -> {
            boundTo.set(new DataChoiceDialog().show(possibleValuesProvider.get()));
        });


        Button newButton = new Button();
        uniformDesign.addIcon(newButton,FontAwesome.Glyph.PLUS);
        newButton.setOnAction(event -> {
            dataEditor.edit(emptyAdder.get());
        });

        Button deleteButton = new Button();
        uniformDesign.addDangerIcon(deleteButton,FontAwesome.Glyph.TIMES);
        deleteButton.setOnAction(event -> boundTo.set(null));
        deleteButton.disableProperty().bind(boundTo.isNull());

        TextField textField = new TextField();
        InvalidationListener invalidationListener = observable -> {
            if (boundTo.get() == null) {
                textField.setText(null);
            } else {
                textField.setText(boundTo.get().getDisplayText());
            }
        };
        invalidationListener.invalidated(boundTo);
        boundTo.addListener(invalidationListener);



        HBox.setHgrow(textField, Priority.ALWAYS);
        textField.setEditable(false);

        textField.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                if (mouseEvent.getClickCount() == 2 && boundTo.get()!=null) {
                    dataEditor.edit(boundTo.get());
                }
            }
        });
        textField.disableProperty().bind(showButton.disabledProperty());

        HBox hBox = new HBox();
        hBox.setSpacing(3);

        hBox.getChildren().add(textField);
        hBox.getChildren().add(showButton);
        hBox.getChildren().add(selectButton);
        hBox.getChildren().add(newButton);
        hBox.getChildren().add(deleteButton);

        return hBox;
    }
}
