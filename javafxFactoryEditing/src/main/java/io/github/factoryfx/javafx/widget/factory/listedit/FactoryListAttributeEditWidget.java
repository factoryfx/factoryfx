package io.github.factoryfx.javafx.widget.factory.listedit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Window;

import org.controlsfx.glyphfont.FontAwesome;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryListBaseAttribute;
import io.github.factoryfx.factory.attribute.dependency.PossibleNewValue;
import io.github.factoryfx.factory.metadata.AttributeMetadata;
import io.github.factoryfx.factory.util.LanguageText;
import io.github.factoryfx.javafx.util.UniformDesign;
import io.github.factoryfx.javafx.widget.Widget;
import io.github.factoryfx.javafx.widget.select.SelectFactoryDialog;

/**
 * Data list edit widget (add Button,delete Button,... )
 * @param <F> Factory
 */
public class FactoryListAttributeEditWidget<RS extends FactoryBase<?,RS>,L, F extends FactoryBase<L,RS>> implements Widget {

    private final LanguageText editText= new LanguageText().en("edit").de("Editieren");
    private final LanguageText selectText= new LanguageText().en("select").de("Auswählen");
    private final LanguageText addText= new LanguageText().en("add").de("Hinzufügen");
    private final LanguageText deleteText= new LanguageText().en("delete").de("Löschen");
    private final LanguageText copyText= new LanguageText().en("copy").de("Kopieren");

    private final LanguageText deleteConfirmationTitle= new LanguageText().en("Delete").de("Löschen");
    private final LanguageText deleteConfirmationHeader= new LanguageText().en("delete item").de("Eintrag löschen");
    private final LanguageText deleteConfirmationContent= new LanguageText().en("delete item?").de("Soll der Eintrag gelöscht werden?");


    private final UniformDesign uniformDesign;
    private final Supplier<List<PossibleNewValue<F>>> newValueProvider;
    private final Supplier<List<PossibleNewValue<F>>> possibleValuesProvider;
    private final boolean isUserEditable;
    private final boolean isUserSelectable;
    private final FactoryListBaseAttribute<L, F,?> factoryListBaseAttribute;
    private final TableView<F> tableView;
    private final Consumer<FactoryBase<?,?>> navigateToData;
    private final BooleanBinding multipleItemsSelected;
    private final boolean isUserCreateable;
    private final BiConsumer<F,List<F>> deleter;

    public FactoryListAttributeEditWidget(FactoryListBaseAttribute<L, F,?> factoryListBaseAttribute, TableView<F> tableView, Consumer<FactoryBase<?,?>> navigateToData, UniformDesign uniformDesign, Supplier<List<PossibleNewValue<F>>> newValueProvider, Supplier<List<PossibleNewValue<F>>> possibleValuesProvider, BiConsumer<F,List<F>> deleter , boolean isUserEditable, boolean isUserSelectable, boolean isUserCreateable) {
        this.uniformDesign = uniformDesign;
        this.newValueProvider = newValueProvider;
        this.possibleValuesProvider = possibleValuesProvider;
        this.isUserEditable = isUserEditable;
        this.isUserSelectable = isUserSelectable;
        this.factoryListBaseAttribute = factoryListBaseAttribute;
        this.tableView = tableView;
        this.navigateToData =  navigateToData;
        this.multipleItemsSelected = Bindings.createBooleanBinding(() -> tableView.getSelectionModel().getSelectedItems().size() > 1, tableView.getSelectionModel().getSelectedItems());
        this.isUserCreateable = isUserCreateable;
        this.deleter= deleter;
    }

    public FactoryListAttributeEditWidget(TableView<F> tableView, Consumer<FactoryBase<?,?>> navigateToData, UniformDesign uniformDesign, FactoryListBaseAttribute<L, F,?> factoryListBaseAttribute, AttributeMetadata attributeMetadata) {
        this(factoryListBaseAttribute, tableView, navigateToData, uniformDesign,
                ()->factoryListBaseAttribute.internal_createNewPossibleValues(attributeMetadata), ()->factoryListBaseAttribute.internal_possibleValues(attributeMetadata), (t, ts) -> factoryListBaseAttribute.internal_deleteFactory(t),
                !factoryListBaseAttribute.internal_isUserReadOnly(), factoryListBaseAttribute.internal_isUserSelectable(), factoryListBaseAttribute.internal_isUserCreatable());
    }

    @Override
    public HBox createContent() {
        Button showButton = new Button("", uniformDesign.createIcon(FontAwesome.Glyph.PENCIL));
        showButton.setOnAction(event -> navigateToData.accept(tableView.getSelectionModel().getSelectedItem()));
        showButton.disableProperty().bind(tableView.getSelectionModel().selectedItemProperty().isNull().or(multipleItemsSelected));

        Button selectButton = new Button("", uniformDesign.createIcon(FontAwesome.Glyph.SEARCH_PLUS));
        selectButton.setOnAction(event -> {
            new SelectFactoryDialog<>(possibleValuesProvider.get(),uniformDesign).show(selectButton.getScene().getWindow(), PossibleNewValue::add);
        });
        selectButton.setDisable(!isUserEditable || !isUserSelectable);

        Button adderButton = new Button();
        uniformDesign.addIcon(adderButton,FontAwesome.Glyph.PLUS);
        adderButton.setOnAction(event -> {
            addNewReference(adderButton.getScene().getWindow());
        });
        adderButton.setDisable(!isUserEditable || !isUserCreateable);

        Button deleteButton = new Button();
        uniformDesign.addDangerIcon(deleteButton,FontAwesome.Glyph.TIMES);
        deleteButton.setOnAction(event -> deleteSelected(deleteButton.getScene().getWindow()));
        deleteButton.disableProperty().bind(tableView.getSelectionModel().selectedItemProperty().isNull().or(new SimpleBooleanProperty(!isUserEditable)));

        Button moveUpButton = new Button();
        uniformDesign.addIcon(moveUpButton,FontAwesome.Glyph.ANGLE_UP);
        moveUpButton.disableProperty().bind(tableView.getSelectionModel().selectedItemProperty().isNull().or(multipleItemsSelected));
        moveUpButton.setOnAction(event -> {
            int selectedIndex = tableView.getSelectionModel().getSelectedIndex();
            if (selectedIndex -1>=0){
                Collections.swap(factoryListBaseAttribute, selectedIndex, selectedIndex -1);
                tableView.getSelectionModel().select(selectedIndex -1);
            }
        });
        Button moveDownButton = new Button();
        uniformDesign.addIcon(moveDownButton,FontAwesome.Glyph.ANGLE_DOWN);
        moveDownButton.disableProperty().bind(tableView.getSelectionModel().selectedItemProperty().isNull().or(multipleItemsSelected));
        moveDownButton.setOnAction(event -> {
            int selectedIndex = tableView.getSelectionModel().getSelectedIndex();
            if (selectedIndex+1< factoryListBaseAttribute.size()){
                Collections.swap(factoryListBaseAttribute, selectedIndex, selectedIndex +1);
                tableView.getSelectionModel().select(selectedIndex +1);
            }
        });
        Button sortButton = new Button();
        uniformDesign.addIcon(sortButton,FontAwesome.Glyph.SORT_ALPHA_ASC);
        sortButton.setOnAction(event -> {
            factoryListBaseAttribute.sort((d1, d2)->{
                String s1 = Optional.ofNullable(d1.internal().getDisplayText()).orElse("");
                String s2 = Optional.ofNullable(d2.internal().getDisplayText()).orElse("");
                return s1.compareTo(s2);
            });
        });
        sortButton.disableProperty().bind(new SimpleBooleanProperty(!isUserEditable));

        Button copyButton = new Button();
        uniformDesign.addIcon(copyButton,FontAwesome.Glyph.COPY);
        copyButton.disableProperty().bind(tableView.getSelectionModel().selectedItemProperty().isNull().or(multipleItemsSelected).or(new SimpleBooleanProperty(!isUserEditable)));
        copyButton.setOnAction(event -> {
            F copy = tableView.getSelectionModel().getSelectedItem().utility().semanticCopy();
            factoryListBaseAttribute.add(copy);
            tableView.getSelectionModel().clearSelection();
            tableView.getSelectionModel().select(copy);
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
        buttons.getChildren().add(sortButton);

        showButton.setTooltip(new Tooltip(uniformDesign.getText(editText)));
        selectButton.setTooltip(new Tooltip(uniformDesign.getText(selectText)));
        adderButton.setTooltip(new Tooltip(uniformDesign.getText(addText)));
        deleteButton.setTooltip(new Tooltip(uniformDesign.getText(deleteText)));
        copyButton.setTooltip(new Tooltip(uniformDesign.getText(copyText)));

        HBox.setMargin(moveUpButton,new Insets(0,0,0,9));
        HBox.setMargin(moveDownButton,new Insets(0,9,0,0));


        tableView.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode()== KeyCode.DELETE){
                deleteSelected(tableView.getScene().getWindow());
            }
            if (event.getCode()== KeyCode.ENTER){
                navigateToData.accept(tableView.getSelectionModel().getSelectedItem());
            }
        });
        return buttons;
    }

    private void deleteSelected(Window owner) {
        if (uniformDesign.isAskBeforeDelete()){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.initOwner(owner);
            alert.setTitle(uniformDesign.getText(deleteConfirmationTitle));
            alert.setHeaderText(uniformDesign.getText(deleteConfirmationHeader));
            alert.setContentText(uniformDesign.getText(deleteConfirmationContent));

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent()){
                if (result.get() != ButtonType.OK){
                    return;
                }
            }
        }

        final List<F> selectedItems = new ArrayList<>(tableView.getSelectionModel().getSelectedItems());
        selectedItems.forEach(t -> deleter.accept(t, factoryListBaseAttribute));
    }

    private void addNewReference(Window owner) {
        List<PossibleNewValue<F>> newDataList = newValueProvider.get();
        if (!newDataList.isEmpty()){
            if (newDataList.size()==1){
                PossibleNewValue<F> first = newDataList.get(0);
                tableView.getSelectionModel().clearSelection();
                first.add();
                tableView.getSelectionModel().select(first.newValue);
                navigateToData.accept(first.newValue);
            } else {
                List<PossibleNewValue<F>> newDataListData = new ArrayList<>(newDataList);
                new SelectFactoryDialog<>(newDataListData,uniformDesign).show(owner, possibleNewValue -> {
                    tableView.getSelectionModel().clearSelection();
                    possibleNewValue.add();
                    tableView.getSelectionModel().select(possibleNewValue.newValue);
                    navigateToData.accept(possibleNewValue.newValue);
                });
            }
        }
    }

}
