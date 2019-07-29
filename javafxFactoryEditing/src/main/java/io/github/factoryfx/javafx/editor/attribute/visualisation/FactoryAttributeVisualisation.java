package io.github.factoryfx.javafx.editor.attribute.visualisation;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryBaseAttribute;
import io.github.factoryfx.javafx.editor.attribute.ValidationDecoration;
import io.github.factoryfx.javafx.widget.select.SelectDataDialog;
import javafx.beans.InvalidationListener;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Window;

import org.controlsfx.glyphfont.FontAwesome;

import io.github.factoryfx.factory.util.LanguageText;
import io.github.factoryfx.javafx.editor.attribute.ValueAttributeVisualisation;
import io.github.factoryfx.javafx.util.UniformDesign;

public class FactoryAttributeVisualisation<T extends FactoryBase<?,?>, A extends FactoryBaseAttribute<?,T,A>> extends ValueAttributeVisualisation<T,A> {

    private LanguageText selectText= new LanguageText().en("select").de("Auswählen");
    private LanguageText addText= new LanguageText().en("add").de("Hinzufügen");
    private LanguageText deleteText= new LanguageText().en("delete").de("Löschen");
    private LanguageText editText= new LanguageText().en("edit").de("Editieren");

    private final UniformDesign uniformDesign;
    private final Consumer<FactoryBase<?,?>> navigateToData;
    private final Supplier<List<T>> newValueProvider;
    private final Supplier<Collection<T>> possibleValuesProvider;
    private final SimpleBooleanProperty isUserSelectable;
    private final SimpleBooleanProperty isUserCreateable;
    private final Runnable remover;
    private final Consumer<T> referenceSetter;



    public FactoryAttributeVisualisation(A attribute, ValidationDecoration validationDecoration, UniformDesign uniformDesign, Consumer<FactoryBase<?,?>> navigateToData) {
        super(attribute,validationDecoration);
        this.uniformDesign = uniformDesign;
        this.navigateToData = navigateToData;

        this.newValueProvider=attribute::internal_createNewPossibleValues;
        this.possibleValuesProvider=attribute::internal_possibleValues;
        this.isUserSelectable=new SimpleBooleanProperty(attribute.internal_isUserSelectable());
        this.isUserCreateable=new SimpleBooleanProperty(attribute.internal_isUserCreatable());
        this.remover=attribute::internal_deleteFactory;
        this.referenceSetter=attribute::set;
    }

    @Override
    public Node createValueVisualisation() {
        Button showButton = new Button("", uniformDesign.createIcon(FontAwesome.Glyph.PENCIL));
        showButton.setOnAction(event -> navigateToData.accept(observableAttributeValue.get()));
        showButton.disableProperty().bind(observableAttributeValue.isNull());


        Button selectButton = new Button();
        selectButton.disableProperty().bind(readOnly.or(isUserSelectable.not()));
        uniformDesign.addIcon(selectButton,FontAwesome.Glyph.SEARCH_PLUS);
        selectButton.setOnAction(event -> {
            Collection<T> collection = possibleValuesProvider.get();
            new SelectDataDialog<>(collection, uniformDesign).show(selectButton.getScene().getWindow(), observableAttributeValue::set);
        });

        Button newButton = new Button();
//        newButton.disableProperty().bind(readOnly.or(isUserCreateable.not()));
        newButton.disableProperty().bind(readOnly);
        uniformDesign.addIcon(newButton,FontAwesome.Glyph.PLUS);
        newButton.setOnAction(event -> addNewReference(newButton.getScene().getWindow()));

        Button deleteButton = new Button();
        deleteButton.disableProperty().bind(readOnly.or(observableAttributeValue.isNull()));
        uniformDesign.addDangerIcon(deleteButton,FontAwesome.Glyph.TIMES);
        deleteButton.setOnAction(event -> remover.run());

        TextField textField = new TextField();
        InvalidationListener invalidationListener = observable -> {
            if (observableAttributeValue.get() == null) {
                textField.setText(null);
            } else {
                textField.setText(observableAttributeValue.get().internal().getDisplayText());
            }
        };
        invalidationListener.invalidated(observableAttributeValue);
        observableAttributeValue.addListener(invalidationListener);



        HBox.setHgrow(textField, Priority.ALWAYS);
        textField.setEditable(false);

        textField.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                if (mouseEvent.getClickCount() == 2 && observableAttributeValue.get()!=null) {
                    navigateToData.accept(observableAttributeValue.get());
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


        hBox.setAlignment(Pos.CENTER_LEFT);
        return hBox;
    }

    private void addNewReference(Window owner) {
        List<T> newData = newValueProvider.get();
        if (!newData.isEmpty()){
            if (newData.size()==1){
                referenceSetter.accept(newData.get(0));
                navigateToData.accept(newData.get(0));
            } else {
                new SelectDataDialog<T>(newData,uniformDesign).show(owner, data -> {
                    referenceSetter.accept(data);
                    navigateToData.accept(data);
                });
            }
        }


    }
}
