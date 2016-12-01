package de.factoryfx.javafx.editor.attribute.visualisation;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import de.factoryfx.data.Data;
import de.factoryfx.data.util.LanguageText;
import de.factoryfx.javafx.editor.attribute.ImmutableAttributeEditorVisualisation;
import de.factoryfx.javafx.editor.data.DataEditor;
import de.factoryfx.javafx.util.DataChoiceDialog;
import de.factoryfx.javafx.util.UniformDesign;
import javafx.beans.InvalidationListener;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import org.controlsfx.glyphfont.FontAwesome;

public class ReferenceAttributeVisualisation extends ImmutableAttributeEditorVisualisation<Data> {

    private LanguageText selectText= new LanguageText().en("select").de("Auswählen");
    private LanguageText addText= new LanguageText().en("add").de("Hinzufügen");
    private LanguageText deleteText= new LanguageText().en("delete").de("Löschen");
    private LanguageText editText= new LanguageText().en("edit").de("Editieren");

    private final UniformDesign uniformDesign;
    private final DataEditor dataEditor;
    private final Supplier<Data> emptyAdder;
    private final Supplier<List<Data>> possibleValuesProvider;
    private final boolean isUserEditable;
    private final boolean isUserSelectable;

    public ReferenceAttributeVisualisation(UniformDesign uniformDesign, DataEditor dataEditor, Supplier<Data> emptyAdder, Supplier<List<Data>> possibleValuesProvider, boolean isUserEditable, boolean isUserSelectable) {
        this.uniformDesign = uniformDesign;
        this.dataEditor = dataEditor;
        this.emptyAdder = emptyAdder;
        this.possibleValuesProvider = possibleValuesProvider;
        this.isUserEditable = isUserEditable;
        this.isUserSelectable = isUserSelectable;
    }

    @Override
    public Node createContent(SimpleObjectProperty<Data> boundTo) {
        Button showButton = new Button("", uniformDesign.createIcon(FontAwesome.Glyph.PENCIL));
        showButton.setOnAction(event -> dataEditor.edit(boundTo.get()));
        showButton.disableProperty().bind(boundTo.isNull());


        Button selectButton = new Button();
        uniformDesign.addIcon(selectButton,FontAwesome.Glyph.SEARCH_PLUS);
        selectButton.setOnAction(event -> {
            final Optional<Data> toAdd = new DataChoiceDialog().show(possibleValuesProvider.get(), selectButton.getScene().getWindow(), uniformDesign);
            toAdd.ifPresent(data -> boundTo.set(data));
        });
        selectButton.setDisable(!isUserEditable || !isUserSelectable);

        Button newButton = new Button();
        uniformDesign.addIcon(newButton,FontAwesome.Glyph.PLUS);
        newButton.setOnAction(event -> {
            dataEditor.edit(emptyAdder.get());
        });
        newButton.setDisable(!isUserEditable);

        Button deleteButton = new Button();
        uniformDesign.addDangerIcon(deleteButton,FontAwesome.Glyph.TIMES);
        deleteButton.setOnAction(event -> boundTo.set(null));
        deleteButton.disableProperty().bind(boundTo.isNull().or(new SimpleBooleanProperty(!isUserEditable)));

        TextField textField = new TextField();
        InvalidationListener invalidationListener = observable -> {
            if (boundTo.get() == null) {
                textField.setText(null);
            } else {
                textField.setText(boundTo.get().internal().getDisplayText());
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

        showButton.setTooltip(new Tooltip(uniformDesign.getText(editText)));
        selectButton.setTooltip(new Tooltip(uniformDesign.getText(selectText)));
        newButton.setTooltip(new Tooltip(uniformDesign.getText(addText)));
        deleteButton.setTooltip(new Tooltip(uniformDesign.getText(deleteText)));

        return hBox;
    }
}
