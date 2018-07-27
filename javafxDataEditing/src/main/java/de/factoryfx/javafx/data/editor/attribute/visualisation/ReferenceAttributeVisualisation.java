package de.factoryfx.javafx.data.editor.attribute.visualisation;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

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
import javafx.stage.Window;

import org.controlsfx.glyphfont.FontAwesome;

import de.factoryfx.data.Data;
import de.factoryfx.data.util.LanguageText;
import de.factoryfx.javafx.data.editor.attribute.ValueAttributeEditorVisualisation;
import de.factoryfx.javafx.data.util.DataChoiceDialog;
import de.factoryfx.javafx.data.util.UniformDesign;
import de.factoryfx.javafx.data.widget.select.SelectDataDialog;

public class ReferenceAttributeVisualisation extends ValueAttributeEditorVisualisation<Data> {

    private LanguageText selectText= new LanguageText().en("select").de("Auswählen");
    private LanguageText addText= new LanguageText().en("add").de("Hinzufügen");
    private LanguageText deleteText= new LanguageText().en("delete").de("Löschen");
    private LanguageText editText= new LanguageText().en("edit").de("Editieren");

    private final UniformDesign uniformDesign;
    private final Consumer<Data> navigateToData;
    private final Supplier<List<Data>> newValueProvider;
    private final Supplier<Collection<? extends Data>> possibleValuesProvider;
    private final boolean isUserEditable;
    private final boolean isUserSelectable;
    private final boolean isUserCreateable;
    private final boolean isCatalogBased;
    private final Runnable remover;
    private final Consumer<Data> referenceSetter;


    public ReferenceAttributeVisualisation(UniformDesign uniformDesign, Consumer<Data> navigateToData, Supplier<List<Data>> newValueProvider, Consumer<Data> referenceSetter, Supplier<Collection<? extends Data>> possibleValuesProvider, Runnable remover, boolean isUserEditable, boolean isUserSelectable, boolean isUserCreateable, boolean isCatalogBased) {
        this.uniformDesign = uniformDesign;
        this.navigateToData = navigateToData;
        this.newValueProvider = newValueProvider;
        this.possibleValuesProvider = possibleValuesProvider;
        this.isUserEditable = isUserEditable;
        this.isUserSelectable = isUserSelectable;
        this.isUserCreateable = isUserCreateable;
        this.remover = remover;
        this.referenceSetter = referenceSetter;
        this.isCatalogBased = isCatalogBased;
    }

    @Override
    public Node createVisualisation(SimpleObjectProperty<Data> boundTo, boolean readonly) {
        Button showButton = new Button("", uniformDesign.createIcon(FontAwesome.Glyph.PENCIL));
        showButton.setOnAction(event -> navigateToData.accept(boundTo.get()));
        showButton.disableProperty().bind(boundTo.isNull());


        Button selectButton = new Button();
        uniformDesign.addIcon(selectButton,FontAwesome.Glyph.SEARCH_PLUS);
        selectButton.setOnAction(event -> {
            final Optional<Data> toAdd = new DataChoiceDialog().show(possibleValuesProvider.get(), selectButton.getScene().getWindow(), uniformDesign);
            toAdd.ifPresent(boundTo::set);
        });
        selectButton.setDisable(!isUserEditable || !isUserSelectable || readonly);

        Button newButton = new Button();
        uniformDesign.addIcon(newButton,FontAwesome.Glyph.PLUS);
        newButton.setOnAction(event -> {
            addNewReference(newButton.getScene().getWindow());
        });
        newButton.setDisable(!isUserEditable || !isUserCreateable || isCatalogBased || readonly);

        Button deleteButton = new Button();
        uniformDesign.addDangerIcon(deleteButton,FontAwesome.Glyph.TIMES);
        deleteButton.setOnAction(event -> remover.run());
        deleteButton.disableProperty().bind(boundTo.isNull().or(new SimpleBooleanProperty(!isUserEditable)).or(new SimpleBooleanProperty(readonly)));

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
                    navigateToData.accept(boundTo.get());
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

    private void addNewReference(Window owner) {
        List<Data> newData = newValueProvider.get();
        if (!newData.isEmpty()){
            if (newData.size()==1){
                referenceSetter.accept(newData.get(0));
                navigateToData.accept(newData.get(0));
            } else {
                new SelectDataDialog(newData,uniformDesign).show(owner, data -> {
                    referenceSetter.accept(data);
                    navigateToData.accept(data);
                });
            }
        }


    }
}
