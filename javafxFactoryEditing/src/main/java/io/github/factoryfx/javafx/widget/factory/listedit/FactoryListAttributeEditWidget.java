package io.github.factoryfx.javafx.widget.factory.listedit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import io.github.factoryfx.factory.FactoryBase;
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

import io.github.factoryfx.factory.attribute.dependency.FactoryListBaseAttribute;
import io.github.factoryfx.factory.util.LanguageText;
import io.github.factoryfx.javafx.util.UniformDesign;
import io.github.factoryfx.javafx.widget.Widget;
import io.github.factoryfx.javafx.widget.select.SelectDataDialog;

/**
 * Data list edit widget (add Button,delete Button,... )
 * @param <F> Factory
 */
public class FactoryListAttributeEditWidget<RS extends FactoryBase<?,RS>,L, F extends FactoryBase<L,RS>> implements Widget {

    private LanguageText editText= new LanguageText().en("edit").de("Editieren");
    private LanguageText selectText= new LanguageText().en("select").de("Auswählen");
    private LanguageText addText= new LanguageText().en("add").de("Hinzufügen");
    private LanguageText deleteText= new LanguageText().en("delete").de("Löschen");
    private LanguageText copyText= new LanguageText().en("copy").de("Kopieren");

    private LanguageText deleteConfirmationTitle= new LanguageText().en("Delete").de("Löschen");
    private LanguageText deleteConfirmationHeader= new LanguageText().en("delete item").de("Eintrag löschen");
    private LanguageText deleteConfirmationContent= new LanguageText().en("delete item?").de("Soll der Eintrag gelöscht werden?");


    private final UniformDesign uniformDesign;
    private final Supplier<List<? extends F>> newValueProvider;
    private final Supplier<Collection<F>> possibleValuesProvider;
    private final boolean isUserEditable;
    private final boolean isUserSelectable;
    private final FactoryListBaseAttribute<L, F,?> factoryListBaseAttribute;
    private final TableView<F> tableView;
    private final Consumer<FactoryBase<?,?>> navigateToData;
    private final BooleanBinding multipleItemsSelected;
    private final boolean isUserCreateable;
    private final BiConsumer<F,List<F>> deleter;

    public FactoryListAttributeEditWidget(FactoryListBaseAttribute<L, F,?> factoryListBaseAttribute, TableView<F> tableView, Consumer<FactoryBase<?,?>> navigateToData, UniformDesign uniformDesign, Supplier<List<? extends F>> newValueProvider, Supplier<Collection<F>> possibleValuesProvider, BiConsumer<F,List<F>> deleter , boolean isUserEditable, boolean isUserSelectable, boolean isUserCreateable) {
        this.uniformDesign = uniformDesign;
        this.newValueProvider = newValueProvider;
        this.possibleValuesProvider = possibleValuesProvider;
        this.isUserEditable = isUserEditable;
        this.isUserSelectable = isUserSelectable;
        this.factoryListBaseAttribute = factoryListBaseAttribute;
        this.tableView = tableView;
        this.navigateToData =  navigateToData;
        multipleItemsSelected = Bindings.createBooleanBinding(() -> tableView.getSelectionModel().getSelectedItems().size() > 1, tableView.getSelectionModel().getSelectedItems());
        this.isUserCreateable = isUserCreateable;
        this.deleter=deleter;
    }

    public FactoryListAttributeEditWidget(TableView<F> tableView, Consumer<FactoryBase<?,?>> navigateToData, UniformDesign uniformDesign, FactoryListBaseAttribute<L, F,?> factoryListBaseAttribute) {
        this(factoryListBaseAttribute, tableView, navigateToData, uniformDesign,
                factoryListBaseAttribute::internal_createNewPossibleValues, factoryListBaseAttribute::internal_possibleValues, (t, ts) -> factoryListBaseAttribute.internal_deleteFactory(t),
                !factoryListBaseAttribute.internal_isUserReadOnly(), factoryListBaseAttribute.internal_isUserSelectable(), factoryListBaseAttribute.internal_isUserCreatable());
    }

    @Override
    public HBox createContent() {
        Button showButton = new Button("", uniformDesign.createIcon(FontAwesome.Glyph.PENCIL));
        showButton.setOnAction(event -> navigateToData.accept(tableView.getSelectionModel().getSelectedItem()));
        showButton.disableProperty().bind(tableView.getSelectionModel().selectedItemProperty().isNull().or(multipleItemsSelected));

        Button selectButton = new Button("", uniformDesign.createIcon(FontAwesome.Glyph.SEARCH_PLUS));
        selectButton.setOnAction(event -> {
            new SelectDataDialog<>(possibleValuesProvider.get(),uniformDesign).show(selectButton.getScene().getWindow(), data -> factoryListBaseAttribute.get().add(data));
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
        List<? extends F> newDataList = newValueProvider.get();
        if (!newDataList.isEmpty()){
            if (newDataList.size()==1){
                factoryListBaseAttribute.add(newDataList.get(0));
                navigateToData.accept(newDataList.get(0));
            } else {
                List<F> newDataListData = new ArrayList<>(newDataList);
                new SelectDataDialog<>(newDataListData,uniformDesign).show(owner, data -> {
                    factoryListBaseAttribute.add(data);
                    navigateToData.accept(data);
                });
            }
        }


    }

}
