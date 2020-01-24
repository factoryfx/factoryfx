package io.github.factoryfx.javafx.editor.attribute.visualisation;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryBaseAttribute;
import io.github.factoryfx.factory.metadata.AttributeMetadata;
import io.github.factoryfx.javafx.editor.attribute.ValidationDecoration;
import io.github.factoryfx.javafx.widget.select.SelectFactoryDialog;
import javafx.beans.InvalidationListener;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Window;

import org.controlsfx.glyphfont.FontAwesome;

import io.github.factoryfx.factory.util.LanguageText;
import io.github.factoryfx.javafx.editor.attribute.ValueAttributeVisualisation;
import io.github.factoryfx.javafx.util.UniformDesign;

public class FactoryAttributeVisualisation<F extends FactoryBase<?,?>, A extends FactoryBaseAttribute<?, F,A>> extends ValueAttributeVisualisation<F,A> {

    private LanguageText addText= new LanguageText().en("add").de("Hinzufügen");
    private LanguageText deleteText= new LanguageText().en("delete").de("Löschen");
    private LanguageText editText= new LanguageText("edit").de("Editieren");


    private LanguageText selectFactoryText= new LanguageText("select factory").de("Factory auswählen");
    private LanguageText selectFactoryCopyText = new LanguageText("select factory copy").de("Factory-Kopie auswählen");
    private LanguageText addNewFactoryText = new LanguageText("add new factory").de("neue Factory hinzufügen");

    private final UniformDesign uniformDesign;
    private final Consumer<FactoryBase<?,?>> navigateToData;
    private final Supplier<List<F>> newValueProvider;
    private final Supplier<Collection<F>> possibleValuesProvider;
    private final SimpleBooleanProperty isUserSelectable;
    private final SimpleBooleanProperty isUserCreateable;
    private final Runnable remover;
    private final Consumer<F> referenceSetter;



    public FactoryAttributeVisualisation(A attribute, AttributeMetadata attributeMetadata, ValidationDecoration validationDecoration, UniformDesign uniformDesign, Consumer<FactoryBase<?,?>> navigateToData) {
        super(attribute,validationDecoration);
        this.uniformDesign = uniformDesign;
        this.navigateToData = navigateToData;

        this.newValueProvider=()->attribute.internal_createNewPossibleValues(attributeMetadata);
        this.possibleValuesProvider=()->attribute.internal_possibleValues(attributeMetadata);
        this.isUserSelectable=new SimpleBooleanProperty(attribute.internal_isUserSelectable());
        this.isUserCreateable=new SimpleBooleanProperty(attribute.internal_isUserCreatable());
        this.remover=attribute::internal_deleteFactory;
        this.referenceSetter=attribute::set;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Node createValueVisualisation() {
        Button showButton = new Button("", uniformDesign.createIcon(FontAwesome.Glyph.PENCIL));
        showButton.setOnAction(event -> navigateToData.accept(observableAttributeValue.get()));
        showButton.disableProperty().bind(observableAttributeValue.isNull());


        MenuButton newButton = new MenuButton();
//        newButton.disableProperty().bind(readOnly.or(isUserCreateable.not()));
        newButton.disableProperty().bind(readOnly);
        uniformDesign.addIcon(newButton,FontAwesome.Glyph.PLUS);


        {
            MenuItem selectFactoryMenuItem = new MenuItem(uniformDesign.getText(selectFactoryText));
            uniformDesign.addIcon(selectFactoryMenuItem, FontAwesome.Glyph.SEARCH_PLUS);
            selectFactoryMenuItem.setOnAction(event -> {
                Collection<F> collection = possibleValuesProvider.get();
                new SelectFactoryDialog<>(collection, uniformDesign).show(newButton.getScene().getWindow(), t -> observableAttributeValue.set((F) t.utility().semanticCopy()));
            });
            newButton.getItems().add(selectFactoryMenuItem);
        }
        {
            MenuItem selectFactoryCopyMenuItem = new MenuItem(uniformDesign.getText(selectFactoryCopyText));
            uniformDesign.addIcon(selectFactoryCopyMenuItem, FontAwesome.Glyph.COPY);
            selectFactoryCopyMenuItem.setOnAction(event -> {
                Collection<F> collection = possibleValuesProvider.get();
                new SelectFactoryDialog<>(collection, uniformDesign).show(newButton.getScene().getWindow(), t -> observableAttributeValue.set((F) t.utility().semanticCopy()));
            });
            newButton.getItems().add(selectFactoryCopyMenuItem);
        }
        {
            MenuItem addNewFactory = new MenuItem(uniformDesign.getText(addNewFactoryText));
            uniformDesign.addIcon(addNewFactory, FontAwesome.Glyph.PLUS);
            addNewFactory.setOnAction(event -> addNewReference(newButton.getScene().getWindow()));
            newButton.getItems().add(addNewFactory);
        }

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
        hBox.getChildren().add(newButton);
        hBox.getChildren().add(deleteButton);

        showButton.setTooltip(new Tooltip(uniformDesign.getText(editText)));
        newButton.setTooltip(new Tooltip(uniformDesign.getText(addText)));
        deleteButton.setTooltip(new Tooltip(uniformDesign.getText(deleteText)));


        hBox.setAlignment(Pos.CENTER_LEFT);
        return hBox;
    }

    private void addNewReference(Window owner) {
        List<F> newData = newValueProvider.get();
        if (!newData.isEmpty()){
            if (newData.size()==1){
                referenceSetter.accept(newData.get(0));
                navigateToData.accept(newData.get(0));
            } else {
                new SelectFactoryDialog<>(newData,uniformDesign).show(owner, data -> {
                    referenceSetter.accept(data);
                    navigateToData.accept(data);
                });
            }
        }


    }
}
