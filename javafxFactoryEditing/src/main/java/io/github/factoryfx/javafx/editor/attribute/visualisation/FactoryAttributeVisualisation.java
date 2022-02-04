package io.github.factoryfx.javafx.editor.attribute.visualisation;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javafx.beans.InvalidationListener;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Window;

import org.controlsfx.glyphfont.FontAwesome;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryBaseAttribute;
import io.github.factoryfx.factory.attribute.dependency.PossibleNewValue;
import io.github.factoryfx.factory.metadata.AttributeMetadata;
import io.github.factoryfx.factory.util.LanguageText;
import io.github.factoryfx.javafx.editor.attribute.ValidationDecoration;
import io.github.factoryfx.javafx.editor.attribute.ValueAttributeVisualisation;
import io.github.factoryfx.javafx.util.UniformDesign;
import io.github.factoryfx.javafx.widget.select.SelectFactoryDialog;

public class FactoryAttributeVisualisation<F extends FactoryBase<?, ?>, A extends FactoryBaseAttribute<?, F, A>> extends ValueAttributeVisualisation<F, A> {

    private final LanguageText addText = new LanguageText().en("add").de("Hinzufügen");
    private final LanguageText deleteText = new LanguageText().en("delete").de("Löschen");
    private final LanguageText editText = new LanguageText("edit").de("Editieren");

    private final LanguageText selectFactoryText = new LanguageText("select factory").de("Factory auswählen");
    private final LanguageText selectFactoryCopyText = new LanguageText("select factory copy").de("Factory-Kopie auswählen");
    private final LanguageText addNewFactoryText = new LanguageText("add new factory").de("neue Factory hinzufügen");

    private final UniformDesign uniformDesign;
    private final Consumer<FactoryBase<?, ?>> navigateToData;
    private final Supplier<List<PossibleNewValue<F>>> newValueProvider;
    private final Supplier<List<PossibleNewValue<F>>> possibleValuesProvider;
    private final SimpleBooleanProperty isUserSelectable;
    private final SimpleBooleanProperty isUserCreateable;
    private final Runnable remover;

    public FactoryAttributeVisualisation(A attribute, AttributeMetadata attributeMetadata, ValidationDecoration validationDecoration, UniformDesign uniformDesign, Consumer<FactoryBase<?, ?>> navigateToData) {
        super(attribute, validationDecoration);
        this.uniformDesign = uniformDesign;
        this.navigateToData = navigateToData;

        this.newValueProvider = () -> attribute.internal_createNewPossibleValues(attributeMetadata);
        this.possibleValuesProvider = () -> attribute.internal_possibleValues(attributeMetadata);
        this.isUserSelectable = new SimpleBooleanProperty(attribute.internal_isUserSelectable());
        this.isUserCreateable = new SimpleBooleanProperty(attribute.internal_isUserCreatable());
        this.remover = attribute::internal_deleteFactory;
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
        uniformDesign.addIcon(newButton, FontAwesome.Glyph.PLUS);

        {
            MenuItem selectFactoryMenuItem = new MenuItem(uniformDesign.getText(selectFactoryText));
            uniformDesign.addIcon(selectFactoryMenuItem, FontAwesome.Glyph.SEARCH_PLUS);
            selectFactoryMenuItem.setOnAction(event -> {
                List<PossibleNewValue<F>> collection = possibleValuesProvider.get();
                new SelectFactoryDialog<>(collection, uniformDesign).show(newButton.getScene().getWindow(), PossibleNewValue::add);
            });
            newButton.getItems().add(selectFactoryMenuItem);
        }
        {
            MenuItem selectFactoryCopyMenuItem = new MenuItem(uniformDesign.getText(selectFactoryCopyText));
            uniformDesign.addIcon(selectFactoryCopyMenuItem, FontAwesome.Glyph.COPY);
            selectFactoryCopyMenuItem.setOnAction(event -> {
                List<PossibleNewValue<F>> collection = possibleValuesProvider.get();
                new SelectFactoryDialog<>(collection, uniformDesign).show(newButton.getScene().getWindow(), PossibleNewValue::addSemanticCopy);
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
        uniformDesign.addDangerIcon(deleteButton, FontAwesome.Glyph.TIMES);
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
                if (mouseEvent.getClickCount() == 2 && observableAttributeValue.get() != null) {
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
        List<PossibleNewValue<F>> newData = newValueProvider.get();
        if (!newData.isEmpty()) {
            if (newData.size() == 1) {
                newData.get(0).add();
                navigateToData.accept(newData.get(0).newValue);
            } else {
                new SelectFactoryDialog<>(newData, uniformDesign).show(owner, data -> {
                    data.add();
                    navigateToData.accept(data.newValue);
                });
            }
        }

    }
}
